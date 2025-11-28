package com.aw.entity;

import com.aw.fill.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@TableName("t_banned_user")
@EqualsAndHashCode(callSuper = true)
public class BannedUser extends BaseEntity {

    /** 被禁言/封号的用户ID */
    private Long userId;

    /** 1=禁言 2=封号 */
    private Integer type;

    private String reason;

    private LocalDateTime endTime;

    /** 操作管理员ID */
    private Long operatorId;
}