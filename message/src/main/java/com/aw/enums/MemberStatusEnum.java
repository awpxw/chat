package com.aw.enums;

import lombok.Getter;

/**
 * 状态枚举（根据业务需要补充）
 */
@Getter
public enum MemberStatusEnum {

    ACTIVE(1, "活跃"),

    MUTED(2, "禁言"),

    EXITED(3, "已退出");

    private final Integer code;

    private final String desc;

    MemberStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static MemberStatusEnum fromCode(Integer code) {
        for (MemberStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return ACTIVE;
    }

}