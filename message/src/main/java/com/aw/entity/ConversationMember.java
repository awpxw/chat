package com.aw.entity;

import com.aw.fill.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 会话成员表 实体类
 */
@Data
@TableName("t_conversation_member")
@EqualsAndHashCode(callSuper = false)
public class ConversationMember extends BaseEntity {

    /**
     * 会话ID
     */
    private Long conversationId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 角色：0-普通 1-管理员 2-隐身人
     */
    private Integer role;

    /**
     * 未读消息数
     */
    private Integer unreadCount;

    /**
     * 最后已读消息ID
     */
    private Long lastReadMsgId;

    /**
     * 最后阅读时间
     */
    private LocalDateTime lastReadTime;

    /**
     * 加入时间
     */
    private LocalDateTime joinedTime;

}