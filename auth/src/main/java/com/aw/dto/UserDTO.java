package com.aw.dto;

import com.aw.dto.groups.UserAddGroup;
import com.aw.dto.groups.UserDeleteGroup;
import com.baomidou.mybatisplus.annotation.TableField;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;

@Data
public class UserDTO {

    @NotNull(groups = {UserAddGroup.class, UserDeleteGroup.class})
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

    private Integer status;

    private Integer isAdmin;

    private String password;

}
