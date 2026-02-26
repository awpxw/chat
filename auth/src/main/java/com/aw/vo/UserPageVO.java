package com.aw.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class UserPageVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String workNo;

    private String name;

    private String nickname;

    private String mobile;

    private String email;

    private String avatar;

    private String position;

    private Integer status;

    private String deptName;

}
