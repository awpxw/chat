package com.aw.service;

import com.aw.dto.ConversationDTO;
import com.aw.vo.ConversationVO;

import java.io.IOException;

public interface ConversationService {

    /**
     * 用户未读消息数
     */
    Integer unreadTotal(ConversationDTO dto);

    /**
     * 创建会话
     */
    Long create(ConversationDTO dto);

    /**
     * 搜索会话（按名称/成员）
     */
    ConversationVO search(ConversationDTO dto);

    /**
     * 退群通知
     */
    void quitNotify(ConversationDTO dto) throws IOException;

}
