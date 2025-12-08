package com.aw.dto;

import com.aw.dto.groups.RoleAddGroup;
import com.aw.dto.groups.RoleDeleteGroup;
import com.aw.dto.groups.RoleUpdateGroup;
import com.aw.page.PageParam;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class RoleDTO extends PageParam {

    @NotNull(groups = {RoleUpdateGroup.class, RoleDeleteGroup.class})
    private Long id;

    @NotNull(groups = RoleAddGroup.class)
    private String name;

    @NotNull(groups = RoleAddGroup.class)
    private String code;


    @NotNull(groups = RoleAddGroup.class)
    @Max(value = 5, groups = RoleAddGroup.class)
    @Min(value = 1, groups = RoleAddGroup.class)
    private Integer dataScope;

    private String remark;

}
