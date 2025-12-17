package com.aw.entity;

import com.aw.fill.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;


@Data
@Builder
@TableName("t_role")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Role extends BaseEntity {

    @Schema(description = "角色名称")
    @TableField("name")
    private String name;

    @Schema(description = "角色编码（唯一）")
    @TableField("code")
    private String code;

    @Schema(description = "数据范围（1全部 2本级及子级 3本级 4仅本人 5自定义）")
    @TableField("data_scope")
    private Integer dataScope;

    @Schema(description = "备注")
    @TableField("remark")
    private String remark;

}