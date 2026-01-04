package com.aw.service;

import com.aw.dto.ForwardMsgDTO;
import com.aw.dto.MessageDTO;

import java.io.IOException;

public interface MessageService {

    /**
     * 保存消息
     */
    void saveMessage(MessageDTO dto);

    /**
     * 转发消息
     */
    void forward(ForwardMsgDTO dto) throws IOException;

}
