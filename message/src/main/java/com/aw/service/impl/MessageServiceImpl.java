package com.aw.service.impl;

import cn.hutool.json.JSONUtil;
import com.aw.dto.*;
import com.aw.entity.*;
import com.aw.enums.*;
import com.aw.exception.BizException;
import com.aw.login.UserContext;
import com.aw.map.MsgMapper;
import com.aw.mapper.ConversationMapper;
import com.aw.mapper.ForwardMsgMapper;
import com.aw.mapper.MessageMapper;
import com.aw.mapper.MessageReactMapper;
import com.aw.redis.RedisUtils;
import com.aw.service.MessageService;
import com.aw.utils.HighlightUtil;
import com.aw.vo.GlobalSearchVO;
import com.aw.vo.MessagePullVO;
import com.aw.ws.*;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    @Resource
    private MessageMapper messageMapper;

    @Resource
    private ForwardMsgMapper forwardMsgMapper;

    @Resource
    private ChatWebSocketHandler chatHandler;

    @Resource
    private ConversationMapper conversationMapper;

    @Resource
    private MsgMapper msgMapper;

    @Resource
    private MessageReactMapper reactMapper;

    @Resource
    private RedisUtils redisUtils;

    @Override
    public void saveMessage(MessageDTO dto) {
        Message msg = Message.builder().build();
        BeanUtils.copyProperties(dto, msg);
        msg.setSenderId(UserContext.get().getUserId());
        int success = messageMapper.insert(msg);
        if (success <= 0) {
            log.error(">>>消息发送失败:{}", dto.getContent());
            throw new BizException("消息发送失败");
        }
    }

    @Override
    public void forward(ForwardMsgDTO dto) throws IOException {

        saveBeforeForward(dto);

        forwardMsgByType(dto);

    }

    private void saveBeforeForward(ForwardMsgDTO dto) {
        Long senderId = UserContext.get().getUserId();
        String content = JSONUtil.toJsonStr(dto.getOriginalMessageIds());
        Message message = Message.builder()
                .conversationId(dto.getTargetUserId())
                .senderId(senderId)
                .content(content)
                .msgTime(LocalDateTime.now())
                .msgType(MessageTypeEnum.FORWARD.getCode())
                .build();
        int success = messageMapper.insert(message);
        if (success <= 0) {
            log.error(">>>转发消息保存失败，msg：{}", JSONUtil.toJsonStr(message));
            throw new BizException("转发消息保存失败");
        }
    }

    @Override
    public void reply(MsgReplyDTO dto) throws IOException {

        saveBeforeReply(dto);

        concatAndReply(dto);

    }

    @Override
    public GlobalSearchVO globalSearch(GlobalSearchDTO dto) {

        List<String> msg = selectAllMsg(dto);

        List<String> highLightMsg = highLight(msg, dto.getKeyword());

        return GlobalSearchVO.builder()
                .highlights(highLightMsg)
                .build();

    }

    @Override
    public MessagePullVO pull(MessagePullDTO dto) {

        return pullMessage(dto);

    }

    @Override
    public void announcement(ConversationDTO dto) {

        updateAnnouncement(dto);

    }

    @Override
    public void react(MessageReactDTO messageReactDTO) throws IOException {

        reactMsg(messageReactDTO);

    }

    private void reactMsg(MessageReactDTO messageReactDTO) throws IOException {
        Long messageId = messageReactDTO.getMessageId();
        MessageReact newMessageReact = MessageReact.builder()
                .messageId(messageId)
                .emoji(messageReactDTO.getEmoji())
                .userId(UserContext.get().getUserId())
                .build();
        reactMapper.insert(newMessageReact);
        //自增
        redisUtils.increment("react:" + messageId + ":" + messageReactDTO.getEmoji());
        //获取所有表情计数
        MessageReactionEmoji[] values = MessageReactionEmoji.values();
        ArrayList<ReactContent.ReactDetail> reactDetails = new ArrayList<>();
        for (MessageReactionEmoji emoji : values) {
            Long num = redisUtils.get("react:" + messageId + ":" + emoji.getOrder());
            ReactContent.ReactDetail reactDetail = ReactContent.ReactDetail.builder()
                    .count(num)
                    .messageId(messageId)
                    .emoji(emoji.getOrder())
                    .build();
            reactDetails.add(reactDetail);
        }
        //ws广播表情计数
        ReactContent reactContent = ReactContent.builder()
                .reactDetails(reactDetails)
                .build();
        Content content = Content.builder()
                .event(WsEventType.REACTION_UPDATE.getCode())
                .reactContent(reactContent)
                .build();
        chatHandler.broadCast(content);
    }

    private void updateAnnouncement(ConversationDTO dto) {
        String announcement = dto.getAnnouncement();
        Long conversationId = dto.getConversationId();
        ChainWrappers.lambdaUpdateChain(Conversation.class)
                .eq(Conversation::getId, conversationId)
                .set(Conversation::getAnnouncement, announcement)
                .update();
    }

    private MessagePullVO pullMessage(MessagePullDTO dto) {
        //未归档的200条最新消息
        Long conversationId = dto.getConversationId();
        List<MessagePullVO.MessageDetail> details = ChainWrappers.lambdaQueryChain(Message.class)
                .eq(Message::getConversationId, conversationId)
                .eq(Message::getIsArchived, false)
                .gt(Message::getMsgTime, LocalDateTime.now().minusDays(7))
                .last("limit 200")
                .orderByAsc(Message::getMsgTime)
                .list()
                .stream()
                .map(msgMapper::toDetail)
                .toList();
        return MessagePullVO.builder().messageDetails(details).build();
    }

    private List<String> selectAllMsg(GlobalSearchDTO dto) {
        Long conversationId = dto.getConversationId();
        List<Message> msg = ChainWrappers.lambdaQueryChain(Message.class)
                .eq(Message::getConversationId, conversationId)
                .list();
        return msg.stream().sorted(Comparator.comparing(Message::getMsgTime)).map(Message::getContent).collect(Collectors.toList());
    }

    private List<String> highLight(List<String> msg, String keyword) {
        return HighlightUtil.highlightKeywords(msg, keyword);
    }

    private void saveBeforeReply(MsgReplyDTO dto) {
        Message message = Message.builder().conversationId(dto.getConversationId())
                .senderId(UserContext.get().getUserId())
                .content(dto.getMsg())
                .msgTime(LocalDateTime.now())
                .extra(null)
                .build();
        int success = messageMapper.insert(message);
        if (success <= 0) {
            log.error(">>>保存消息失败，msg：{}", JSONUtil.toJsonStr(message));
            throw new BizException("保存消息失败");
        }
    }

    /**
     * 回复消息
     */
    private void concatAndReply(MsgReplyDTO dto) throws IOException {
        Long fromId = UserContext.get().getUserId();
        Long conversationId = dto.getConversationId();
        //判断会话类型
        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null) {
            log.error(">>>未找到会话，id：{}", conversationId);
            throw new BizException("未找到会话");
        }
        if (ConversationTypeEnum.isGroup(conversation.getType())) {
            //回复群聊
            List<Long> memberIds = getMemberIds(conversation.getId());
            for (Long toId : memberIds) {
                ReplyContent replyContent = ReplyContent.replyContent(true, fromId, toId, dto.getMsg(), dto.getReferenceMsg());
                chatHandler.replyMessage(replyContent, Collections.singletonList(toId));
            }
        } else if (ConversationTypeEnum.isSingle(conversation.getType())) {
            //回复私聊
            List<Long> memberIds = getMemberIds(conversation.getId());
            ReplyContent replyContent = ReplyContent.replyContent(false, fromId, memberIds.get(0), dto.getMsg(), dto.getReferenceMsg());
            chatHandler.replyMessage(replyContent, Collections.singletonList(memberIds.get(0)));
        } else {
            log.error(">>>不支持的会话类型，type：{}", conversation.getType());
            throw new BizException("不支持的会话类型");
        }
    }

    private void forwardMsgByType(ForwardMsgDTO dto) throws IOException {

        List<Message> messages = saveForwardRecordAndMsg(dto);

        forwardMsg(messages, dto);

    }

    /**
     * 消息转发（核心方法）
     */
    private void forwardMsg(List<Message> messages, ForwardMsgDTO dto) throws IOException {
        List<String> msgStr = messages.stream().map(JSONUtil::toJsonStr).toList();
        Integer forwardType = dto.getForwardType();
        if (ForwardTypeEnum.isSeparateSingle(forwardType)) {
            //私聊逐条转发
            ForwardContent forwardContent = ForwardContent.forwardContent(forwardType, UserContext.get().getUserId(), dto.getTargetUserId(), msgStr);
            chatHandler.forwardMessage(forwardContent, Collections.singletonList(dto.getTargetUserId()));
        } else if (ForwardTypeEnum.isSeparateGroup(forwardType)) {
            //群聊逐条转发
            List<Long> userIds = getMemberIds(dto.getTargetGroupId());
            for (Long userId : userIds) {
                ForwardContent forwardContent = ForwardContent.forwardContent(forwardType, UserContext.get().getUserId(), userId, msgStr);
                chatHandler.forwardMessage(forwardContent, Collections.singletonList(dto.getTargetUserId()));
            }
        } else if (ForwardTypeEnum.isMergedGroup(forwardType)) {
            //群聊合并转发
            List<Long> userIds = getMemberIds(dto.getTargetGroupId());
            for (Long userId : userIds) {
                ForwardContent forwardContent = ForwardContent.forwardContent(forwardType, UserContext.get().getUserId(), userId, msgStr);
                chatHandler.forwardMessage(forwardContent, Collections.singletonList(dto.getTargetUserId()));
            }
        } else if (ForwardTypeEnum.isMergedSingle(forwardType)) {
            //私聊逐条转发
            ForwardContent forwardContent = ForwardContent.forwardContent(forwardType, UserContext.get().getUserId(), dto.getTargetUserId(), msgStr);
            chatHandler.forwardMessage(forwardContent, Collections.singletonList(dto.getTargetUserId()));
        } else {
            throw new BizException(">>>unsupported forward type:" + forwardType);
        }
    }

    private List<Long> getMemberIds(Long conversationId) {
        return ChainWrappers.lambdaQueryChain(ConversationMember.class).eq(ConversationMember::getConversationId, conversationId).list().stream().map(ConversationMember::getUserId).toList();
    }

    private List<Message> saveForwardRecordAndMsg(ForwardMsgDTO dto) {
        Integer forwardType = dto.getForwardType();
        List<Message> messages;
        if (ForwardTypeEnum.isSeparateGroup(forwardType)) {
            //群聊-逐条转发
            messages = saveSeparate(dto, null);
        } else if (ForwardTypeEnum.isSeparateSingle(forwardType)) {
            //私聊-逐条转发
            Long conversationId = selectConversation(dto.getTargetUserId(), dto.getTargetUserName());
            messages = saveSeparate(dto, conversationId);
        } else if (ForwardTypeEnum.isMergedGroup(forwardType)) {
            //群聊-合并转发
            messages = saveMerged(dto, null);
        } else if (ForwardTypeEnum.isMergedSingle(forwardType)) {
            //私聊-合并转发
            Long conversationId = selectConversation(dto.getTargetUserId(), dto.getTargetUserName());
            messages = saveMerged(dto, conversationId);
        } else {
            log.error(">>>不支持的转发类型：{}", dto.getForwardType());
            throw new BizException("不支持的转发类型:" + dto.getForwardType());
        }
        return messages;
    }

    private List<Message> saveSeparate(ForwardMsgDTO dto, Long conversationId) {
        List<Message> originMsg = selectMsgByIds(dto.getOriginalMessageIds());
        Message message = Message.builder().build();
        ForwardMsg forwardMsg = new ForwardMsg();
        List<Message> messages = new ArrayList<>();
        List<ForwardMsg> forwardMessages = new ArrayList<>();
        for (Message msg : originMsg) {
            message.setConversationId(conversationId == null ? dto.getTargetGroupId() : conversationId).setSenderId(UserContext.get().getUserId()).setMsgType(MessageTypeEnum.FORWARD.getCode()).setContent(msg.getContent()).setExtra(msg.getExtra()).setMsgTime(LocalDateTime.now()).setStatus(MessageStatusEnum.UNREAD.getCode());
            forwardMsg.setMsgId(msg.getId()).setForwardUserId(UserContext.get().getUserId()).setForwardTime(LocalDateTime.now());
            messages.add(message);
            forwardMessages.add(forwardMsg);
        }
        try {
            forwardMsgMapper.batchInsert(forwardMessages, UserContext.get());
            messageMapper.batchInsert(messages, UserContext.get());
        } catch (Exception e) {
            log.error(">>>转发消息失败,id:{}", dto.getTargetGroupId());
            throw new BizException("转发消息失败");
        }
        return messages;
    }

    private List<Message> saveMerged(ForwardMsgDTO dto, Long conversationId) {
        List<Message> originMsg = selectMsgByIds(dto.getOriginalMessageIds());
        Message message = Message.builder().build();
        ForwardMsg forwardMsg = new ForwardMsg();
        message.setConversationId(conversationId == null ? dto.getTargetGroupId() : conversationId).setSenderId(UserContext.get().getUserId()).setMsgType(MessageTypeEnum.FORWARD.getCode()).setContent(JSONUtil.toJsonStr(originMsg)).setMsgTime(LocalDateTime.now()).setStatus(MessageStatusEnum.UNREAD.getCode());
        List<Long> msgIdList = originMsg.stream().map(Message::getId).toList();
        forwardMsg.setMsgIdList(JSONUtil.toJsonStr(msgIdList)).setForwardUserId(UserContext.get().getUserId()).setForwardTime(LocalDateTime.now());
        int msgSuccess = messageMapper.insert(message);
        int forwardSuccess = forwardMsgMapper.insert(forwardMsg);
        if (msgSuccess <= 0 || forwardSuccess <= 0) {
            log.error(">>>合并转发失败:{}", dto.getOriginalMessageIds());
            throw new BizException("合并转发失败");
        }
        return Collections.singletonList(message);
    }

    private Long selectConversation(Long toUser, String userName) {
        Long fromUser = UserContext.get().getUserId();
        Long conversationId = forwardMsgMapper.findConversationByUserId(toUser, fromUser);
        if (conversationId == null) {
            conversationId = createConversation(userName);
        }
        return conversationId;
    }

    private Long createConversation(String userName) {
        Long creatorId = UserContext.get().getUserId();
        //私聊以用户名作为会话名
        Conversation conversation = Conversation.builder()
                .creatorId(creatorId)
                .name(userName)
                .type(ConversationTypeEnum.SINGLE.getCode())
                .isPinned(true)
                .mute(false)
                .build();
        int insert = conversationMapper.insert(conversation);
        if (insert != 1) {
            log.error(">>>创建会话失败");
            throw new BizException("创建会话失败");
        }
        return conversation.getId();
    }

    private List<Message> selectMsgByIds(List<Long> msgIds) {
        List<Message> messages = ChainWrappers.lambdaQueryChain(Message.class).in(Message::getId, msgIds).list();
        if (messages == null || messages.isEmpty()) {
            log.error(">>>消息不存在，id：【{}】", msgIds);
            throw new BizException("消息不存在");
        }
        return messages;
    }

}
