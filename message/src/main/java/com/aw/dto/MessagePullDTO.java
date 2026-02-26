package com.aw.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessagePullDTO {

    /**
     * 会话id
     */
    private Long conversationId;

}
