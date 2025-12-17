package com.aw.dto;

import com.aw.dto.groups.*;
import com.aw.page.PageParam;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserDTO extends PageParam {

    @NotNull(groups = {UserDeleteGroup.class, UserBanGroup.class, UserMenuTreeGroup.class, UserKickGroup.class})
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @NotNull(groups = UserAddGroup.class)
    private Long deptId;

    private String workNo;

    private String name;

    private String nickname;

    private String mobile;

    private String email;

    private String avatar;

    private String position;

    @NotNull(groups = {UserBanGroup.class})
    @Min(0)
    @Max(1)
    private Integer status;

    @NotNull(groups = {UserAddGroup.class})
    @Min(0)
    @Max(1)
    private Integer originStatus;

    private Integer isAdmin;

    private String password;

    @NotNull(groups = {UserAllotRoleGroup.class})
    private List<Long> roleIds;


}
