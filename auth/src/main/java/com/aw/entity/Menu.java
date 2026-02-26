package com.aw.entity;

import com.aw.fill.BaseEntity;
import com.aw.utils.tree.ITree;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;


@Data
@TableName("t_menu")
@EqualsAndHashCode(callSuper = true)
public class Menu extends BaseEntity {

    @Schema(description = "父菜单ID，一级菜单为0")
    @TableField("parent_id")
    private Long parentId;

    @Schema(description = "菜单名称")
    private String name;

    @Schema(description = "路由地址")
    private String path;

    @Schema(description = "组件路径")
    private String component;

    @Schema(description = "权限标识")
    private String perms;

    @Schema(description = "菜单类型（0目录 1菜单 2按钮）")
    private Integer type;

    @Schema(description = "菜单图标")
    private String icon;

    @Schema(description = "显示排序")
    private Integer sort;

    @Schema(description = "是否显示（1显示 0隐藏）")
    private Integer visible;

}