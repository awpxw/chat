package com.aw.enums;

import lombok.Getter;

/**
 * 消息类型枚举
 */
@Getter
public enum MessageTypeEnum {

    TEXT(1, "文本"),

    IMAGE(2, "图片"),

    FILE(3, "文件"),

    VOICE(4, "语音"),

    VIDEO(5, "视频"),

    RECALL(6, "撤回"),

    SYSTEM(7, "系统消息"),

    READ_RECEIPT(8, "已读回执");

    private final Integer code;

    private final String desc;

    MessageTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据code获取枚举
     */
    public static MessageTypeEnum fromCode(Integer code) {
        if (code == null) {
            return TEXT;
        }
        for (MessageTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return TEXT;
    }

}