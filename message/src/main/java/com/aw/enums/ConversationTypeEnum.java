package com.aw.enums;

import lombok.Getter;

/**
 * 会话类型枚举
 */
@Getter
public enum ConversationTypeEnum {

    SINGLE(1, "单聊"),

    GROUP(2, "群聊"),

    CUSTOMER(3, "客服");

    private final Integer code;

    private final String name;

    ConversationTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据code获取枚举
     */
    public static ConversationTypeEnum fromCode(Integer code) {
        if (code == null) {
            return SINGLE;
        }
        for (ConversationTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return SINGLE;
    }

}