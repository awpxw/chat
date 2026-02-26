package com.aw.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GlobalSearchDTO {

    @NotNull
    private Long conversationId;

    @NotNull
    private String keyword;

}
