package com.aw.dto;

import com.aw.dto.groups.DeptAddGroup;
import com.aw.dto.groups.DeptDeleteGroup;
import com.aw.dto.groups.DeptUpdateGroup;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
public class DeptDTO {

    @NotNull(message = "不为空", groups = {DeptDeleteGroup.class})
    private Long id;

    @NotNull(message = "不为空", groups = {DeptAddGroup.class})
    private Long parentId;

    @NotNull(message = "不为空", groups = {DeptUpdateGroup.class, DeptAddGroup.class})
    private String name;

    @NotNull(message = "不为空", groups = {DeptUpdateGroup.class, DeptAddGroup.class})
    private Integer sort;

    @NotNull(message = "不为空", groups = {DeptUpdateGroup.class, DeptAddGroup.class})
    @Max(1)
    @Min(0)
    private Integer status;

}
