package com.aw.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class UserDetailVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private Integer status;

    private String workNo;

    private String name;

    private String nickname;

    private String mobile;

    private String email;

    private String avatar;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long deptId;

    private String position;

    private Integer isAdmin;

    private String password;

    private String deptName;

}
