package com.aw.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 消息转发
 */
@Data
public class ForwardMsgDTO {

    @Schema(description = "转发类型")
    private Integer forwardType;

    @Schema(description = "原消息ID列表（必填，支持1条或多条）")
    private List<Long> originalMessageIds;

    @Schema(description = "转发用户id")
    private Long targetUserId;

    @Schema(description = "转发群id")
    private Long targetGroupId;

}
