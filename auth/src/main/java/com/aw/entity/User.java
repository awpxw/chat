package com.aw.entity;

import com.aw.fill.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 员工表实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_user")
public class User extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 工号
     */
    @TableField("work_no")
    private String workNo;

    /**
     * 姓名
     */
    @TableField("name")
    private String name;

    /**
     * 昵称
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 手机号
     */
    @TableField("mobile")
    private String mobile;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 头像
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 部门ID
     */
    @TableField("dept_id")
    private Long deptId;

    /**
     * 职位
     */
    @TableField("position")
    private String position;

    /**
     * 是否管理员：0否 1是
     */
    @TableField("is_admin")
    private Integer isAdmin;

    /**
     * 密码
     */
    @TableField("password")
    private String password;

}