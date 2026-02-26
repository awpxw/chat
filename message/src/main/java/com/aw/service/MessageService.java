package com.aw.service;

import com.aw.dto.*;
import com.aw.vo.GlobalSearchVO;
import com.aw.vo.MessagePullVO;

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

    /**
     * 回复消息
     */
    void reply(MsgReplyDTO dto) throws IOException;

    /**
     * 全局搜索
     */
    GlobalSearchVO globalSearch(GlobalSearchDTO dto);

    /**
     * 拉取消息
     */
    MessagePullVO pull(MessagePullDTO dto);

}
