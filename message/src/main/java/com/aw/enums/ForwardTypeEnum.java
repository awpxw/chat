package com.aw.enums;

import lombok.Getter;

@Getter
public enum ForwardTypeEnum {

    SEPARATE_SINGLE(1, "私聊-逐条转发"),   // 多条逐条转发（每条独立成一条转发消息）

    SEPARATE_GROUP(2, "群聊-逐条转发"),

    MERGED_SINGLE(3, "私聊-合并转发"),

    MERGED_GROUP(4, "群聊-合并转发");

    private final int code;

    private final String msg;

    ForwardTypeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static Boolean isSeparateSingle(Integer code) {
        if (code != null) {
            return code == SEPARATE_SINGLE.code;
        }
        return false;
    }

    public static Boolean isSeparateGroup(Integer code) {
        if (code != null) {
            return code == SEPARATE_GROUP.code;
        }
        return false;
    }

    public static Boolean isMergedSingle(Integer code) {
        if (code != null) {
            return code == MERGED_SINGLE.code;
        }
        return false;
    }

    public static Boolean isMergedGroup(Integer code) {
        if (code != null) {
            return code == MERGED_GROUP.code;
        }
        return false;
    }


}
