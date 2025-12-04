package com.aw.dto;

import com.aw.dto.groups.DeptAddGroup;
import com.aw.dto.groups.DeptDeleteGroup;
import com.aw.dto.groups.DeptUpdateGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
public class DeptDTO {

    @NotNull(message = "id不为空", groups = {DeptDeleteGroup.class})
    private Long id;

    @NotBlank(message = "parentId不为空", groups = {DeptAddGroup.class})
    private Long parentId;

    @NotBlank(message = "name不为空", groups = {DeptUpdateGroup.class, DeptAddGroup.class})
    private String name;

}
