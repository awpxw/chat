package com.aw.enums;

import lombok.Getter;

/**
 * 消息状态枚举
 */
@Getter
public enum MessageStatusEnum {

    SENDING(0, "发送中"),

    SENT(1, "已发送"),

    DELIVERED(2, "已送达"),

    READ(3, "已读"),

    FAILED(4, "发送失败"),

    RECALLED(5, "已撤回"),

    DELETED(6, "已删除"),

    UNREAD(7, "未读");

    private final Integer code;

    private final String desc;

    MessageStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据code获取枚举
     */
    public static MessageStatusEnum getByCode(Integer code) {
        if (code == null) {
            return SENT;
        }
        for (MessageStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return SENT;
    }

    /**
     * 判断消息是否已送达（包括已送达和已读）
     */
    public boolean isDelivered() {
        return this == DELIVERED || this == READ;
    }

    /**
     * 判断消息是否可撤回
     */
    public boolean canRecall() {
        return this == SENT || this == DELIVERED || this == READ;
    }

}