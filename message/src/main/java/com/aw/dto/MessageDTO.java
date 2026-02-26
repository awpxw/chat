package com.aw.dto;

import lombok.Data;

@Data
public class MessageDTO {

    /**
     * 会话id
     */
    private Long conversationId;

    /**
     * 发送人
     */
    private Long senderId;

    /**
     * 消息类型
     */
    private Integer msgType;

    /**
     * 内容
     */
    private String content;

    /**
     * 额外
     */
    private Object extra;

    /**
     * 是否是自己发送的消息
     */
    private Boolean isSelf;

}
