package com.aw.dto;

import lombok.Data;

@Data
public class MsgReplyDTO {

    /**
     * 会话id
     */
    private Long conversationId;

    /**
     * 消息Id
     */
    private Long msgId;

    /**
     * 消息内容
     */
    private String msg;

    /**
     * 被引用消息
     */
    private String referenceMsg;

}
