package com.aw.service.impl;

import cn.hutool.json.JSONUtil;
import com.aw.dto.ForwardMsgDTO;
import com.aw.dto.MessageDTO;
import com.aw.entity.ConversationMember;
import com.aw.entity.ForwardMsg;
import com.aw.entity.Message;
import com.aw.enums.ForwardTypeEnum;
import com.aw.enums.MessageStatusEnum;
import com.aw.enums.MessageTypeEnum;
import com.aw.exception.BizException;
import com.aw.login.UserContext;
import com.aw.mapper.ForwardMsgMapper;
import com.aw.mapper.MessageMapper;
import com.aw.service.MessageService;
import com.aw.ws.ChatWebSocketHandler;
import com.aw.ws.ForwardContent;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
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

    @Override
    public void saveMessage(MessageDTO dto) {
        Message msg = new Message();
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

        forwardMsgByType(dto);

    }

    private void forwardMsgByType(ForwardMsgDTO dto) throws IOException {

        List<Message> messages = saveForwardRecordAndMsg(dto);

        pushMsg(messages, dto);

    }

    /**
     * 消息转发（核心方法）
     */
    private void pushMsg(List<Message> messages, ForwardMsgDTO dto) throws IOException {
        List<String> msgStr = messages.stream().map(JSONUtil::toJsonStr).toList();
        Integer forwardType = dto.getForwardType();
        if (ForwardTypeEnum.isSeparateSingle(forwardType)) {
            //私聊逐条转发
            ForwardContent forwardContent = ForwardContent.forwardContent(forwardType, UserContext.get().getUserId(), dto.getTargetUserId(), msgStr);
            chatHandler.forwardMessage(forwardContent, Collections.singletonList(dto.getTargetUserId()));
        } else if (ForwardTypeEnum.isSeparateGroup(forwardType)) {
            //群聊逐条转发
            List<Long> userIds = getMemberIds(dto);
            for (Long userId : userIds) {
                ForwardContent forwardContent = ForwardContent.forwardContent(forwardType, UserContext.get().getUserId(), userId, msgStr);
                chatHandler.forwardMessage(forwardContent, Collections.singletonList(dto.getTargetUserId()));
            }
        } else if (ForwardTypeEnum.isMergedGroup(forwardType)) {
            //群聊合并转发
            List<Long> userIds = getMemberIds(dto);
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

    private List<Long> getMemberIds(ForwardMsgDTO dto) {
        return ChainWrappers.lambdaQueryChain(ConversationMember.class).eq(ConversationMember::getConversationId, dto.getTargetGroupId()).list().stream().map(ConversationMember::getUserId).toList();
    }

    private List<Message> saveForwardRecordAndMsg(ForwardMsgDTO dto) {
        Integer forwardType = dto.getForwardType();
        List<Message> messages;
        if (ForwardTypeEnum.isSeparateGroup(forwardType)) {
            //群聊-逐条转发
            messages = saveSeparate(dto, null);
        } else if (ForwardTypeEnum.isSeparateSingle(forwardType)) {
            //私聊-逐条转发
            Long conversationId = selectConversation(dto.getTargetUserId());
            messages = saveSeparate(dto, conversationId);
        } else if (ForwardTypeEnum.isMergedGroup(forwardType)) {
            //群聊-合并转发
            messages = saveMerged(dto, null);
        } else if (ForwardTypeEnum.isMergedSingle(forwardType)) {
            //私聊-合并转发
            Long conversationId = selectConversation(dto.getTargetUserId());
            messages = saveMerged(dto, conversationId);
        } else {
            log.error(">>>不支持的转发类型：{}", dto.getForwardType());
            throw new BizException("不支持的转发类型:" + dto.getForwardType());
        }
        return messages;
    }

    private List<Message> saveSeparate(ForwardMsgDTO dto, Long conversationId) {
        List<Message> originMsg = selectMsgByIds(dto.getOriginalMessageIds());
        Message message = new Message();
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
        Message message = new Message();
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

    private Long selectConversation(Long toUser) {
        Long fromUser = UserContext.get().getUserId();
        Long conversationId = forwardMsgMapper.findConversationByUserId(toUser, fromUser);
        if (conversationId == null) {
            conversationId = createConversation();
        }
        return conversationId;
    }

    private Long createConversation() {
        //todo 创建会话
        return 1L;
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
