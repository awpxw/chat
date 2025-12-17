package com.aw.dto;

import com.aw.dto.groups.*;
import com.aw.page.PageParam;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


@Data
@EqualsAndHashCode(callSuper = true)
public class RoleDTO extends PageParam {

    @NotNull(groups = {RoleUpdateGroup.class, RoleDeleteGroup.class, RoleAllotGroup.class,RoleCancelGroup.class})
    private Long id;

    @NotNull(groups = RoleAddGroup.class)
    private String name;

    @NotNull(groups = RoleAddGroup.class)
    private String code;

    @NotNull(groups = {RoleAllotGroup.class, RoleCancelGroup.class})
    @NotEmpty(groups = {RoleAllotGroup.class, RoleCancelGroup.class})
    private List<Long> menuIds;

    @NotNull(groups = RoleAddGroup.class)
    @Max(value = 5, groups = RoleAddGroup.class)
    @Min(value = 1, groups = RoleAddGroup.class)
    private Integer dataScope;

    private String remark;

}
