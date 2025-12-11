package com.aw.entity;

import com.aw.fill.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;


@Data
@Builder
@TableName("t_role_menu")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RoleMenu extends BaseEntity {

    @Schema(description = "角色id")
    @TableField("role_id")
    private Long roleId;

    @Schema(description = "菜单id")
    @TableField("menu_id")
    private Long menuId;

}
