package com.aw.dto;

import com.aw.dto.group.MemberAdd;
import com.aw.dto.group.MemberList;
import com.aw.dto.group.MemberRemove;
import com.aw.dto.group.MemberRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class MemberDTO {

    /**
     * 会话id
     */
    @NotNull(groups = { MemberAdd.class , MemberList.class, MemberRemove.class,MemberRole.class})
    private Long conversionId;

    /**
     * 用户id
     */
    @NotNull(groups = {MemberRemove.class,MemberRole.class})
    private Long userId;

    /**
     * 添加用户id
     */
    @NotNull(groups = {MemberAdd.class})
    private List<Long> userIds;

    /**
     * 设置用户权限
     */
    @NotNull(groups = {MemberRole.class})
    private Integer memberRole;

}
