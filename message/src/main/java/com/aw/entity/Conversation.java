package com.aw.entity;

import com.aw.fill.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_conversation")
public class Conversation extends BaseEntity {

    /**
     * 会话类型：1单聊 2群聊 3客服会话
     */
    private Integer type;

    /**
     * 会话名称（群聊必填，单聊可选为对方备注名）
     */
    private String name;

    /**
     * 会话头像URL
     */
    private String avatar;

    /**
     * 创建者ID
     */
    private Long creatorId;

    /**
     * 成员数量，默认2（单聊）
     */
    private Integer memberCount = 2;

    /**
     * 是否置顶 0否 1是
     */
    private Boolean isPinned = false;

    /**
     * 是否免打扰 0否 1是
     */
    private Boolean mute;

}