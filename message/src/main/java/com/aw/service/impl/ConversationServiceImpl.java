package com.aw.service.impl;

import com.aw.dto.ConversationDTO;
import com.aw.entity.Conversation;
import com.aw.entity.ConversationMember;
import com.aw.exception.BizException;
import com.aw.login.UserContext;
import com.aw.mapper.ConversationMapper;
import com.aw.service.ConversationService;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ConversationServiceImpl implements ConversationService {

    @Resource
    private ConversationMapper conversationMapper;

    @Override
    public Long create(ConversationDTO dto) {
        String name = Strings.join(dto.getInitialUserName(), ',');
        Long creatorId = UserContext.get().getUserId();
        Conversation conversation = Conversation.builder()
                .creatorId(creatorId)
                .name(name)
                .type(dto.getType())
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

    @Override
    public Integer unreadTotal(ConversationDTO dto) {
        Long userId = dto.getUserId();
        Long conversationId = dto.getConversationId();
        ConversationMember member = ChainWrappers.lambdaQueryChain(ConversationMember.class)
                .eq(ConversationMember::getConversationId, conversationId)
                .eq(ConversationMember::getUserId, userId)
                .last("limit 1")
                .one();
        if (member == null) {
            log.error(">>>用户不存在，用户id：{}", userId);
            throw new BizException("用户不存在");
        }
        Integer unreadCount = member.getUnreadCount();
        return unreadCount == null ? 0 : unreadCount;
    }


}
