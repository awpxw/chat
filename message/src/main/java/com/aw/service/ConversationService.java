package com.aw.service;

import com.aw.dto.ConversationDTO;

public interface ConversationService {

    /**
     * 用户未读消息数
     */
    Integer unreadTotal(ConversationDTO dto);

}
