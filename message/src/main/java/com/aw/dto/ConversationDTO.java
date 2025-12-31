package com.aw.dto;

import com.aw.dto.group.ConversationUnreadTotal;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConversationDTO {

    @NotNull(groups = {ConversationUnreadTotal.class})
    private Long conversationId;

    @NotNull(groups = {ConversationUnreadTotal.class})
    private Long userId;

}
