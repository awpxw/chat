package com.aw.enums;

import lombok.Getter;

/**
 * 角色枚举
 */
@Getter
public enum RoleEnum {

    NORMAL(0, "普通成员"),

    ADMIN(1, "管理员"),

    INVISIBLE(2, "隐身人");

    private final Integer code;
    private final String desc;

    RoleEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RoleEnum fromCode(Integer code) {
        for (RoleEnum role : values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        return NORMAL;
    }

}