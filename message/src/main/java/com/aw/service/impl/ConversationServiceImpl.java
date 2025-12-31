package com.aw.service.impl;

import com.aw.dto.ConversationDTO;
import com.aw.entity.ConversationMember;
import com.aw.exception.BizException;
import com.aw.service.ConversationService;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ConversationServiceImpl implements ConversationService {

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
