package com.aw.dto;

import com.aw.dto.group.ConversationCreate;
import com.aw.dto.group.ConversationUnreadTotal;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ConversationDTO {

    @NotNull(groups = {ConversationUnreadTotal.class})
    private Long conversationId;

    @NotNull(groups = {ConversationUnreadTotal.class})
    private Long userId;

    @NotNull(groups = {ConversationCreate.class})
    private List<String> initialUserName;

    @NotNull(groups = {ConversationCreate.class})
    private Integer type;

    @NotNull
    private String memberOrName;

    private String announcement;

}
