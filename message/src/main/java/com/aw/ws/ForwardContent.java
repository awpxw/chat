package com.aw.ws;

import com.aw.enums.ForwardTypeEnum;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 转发消息载体
 */
@Data
@Builder
public class ForwardContent {

    /**
     * 转发人Id
     */
    private Long fromId;

    /**
     * 接收人或群聊Id
     */
    private Long toId;

    /**
     * 转发类型
     */
    private Integer forwardType;

    /**
     * 消息
     */
    private List<String> messages;

    public static ForwardContent forwardContent(Integer forwardType, Long fromId, Long toId, List<String> messages) {
        return ForwardContent.builder()
                .forwardType(forwardType)
                .fromId(fromId)
                .toId(toId)
                .messages(messages)
                .build();
    }


}
