package com.aw.entity;

import com.aw.fill.BaseEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageReact extends BaseEntity {

    /**
     * 消息id
     */
    private Long messageId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 表情
     */
    private Integer emoji;

}
