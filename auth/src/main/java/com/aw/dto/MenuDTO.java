package com.aw.dto;

import com.aw.dto.groups.MenuAddGroup;
import com.aw.dto.groups.MenuDeleteGroup;
import com.aw.dto.groups.MenuUpdateGroup;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MenuDTO {

    @NotNull(groups = {MenuUpdateGroup.class, MenuDeleteGroup.class})
    private Long id;

    @NotNull(groups = {MenuAddGroup.class})
    private Long parentId;

    @NotNull(groups = {MenuAddGroup.class})
    private String name;

    @NotNull(groups = {MenuAddGroup.class})
    private String path;

    @NotNull(groups = {MenuAddGroup.class})
    private String component;

    @NotNull(groups = {MenuAddGroup.class})
    private String perms;

    @NotNull(groups = {MenuAddGroup.class})
    private Integer type;

    @NotNull(groups = {MenuAddGroup.class})
    private String icon;

    @NotNull(groups = {MenuAddGroup.class})
    private Integer sort;

    @NotNull(groups = {MenuAddGroup.class})
    private Integer visible;

}
