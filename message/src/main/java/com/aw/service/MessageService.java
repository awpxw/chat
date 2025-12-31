package com.aw.service;

import com.aw.dto.MessageDTO;

public interface MessageService {

    /**
     * 保存消息
     */
    void saveMessage(MessageDTO dto);

}
