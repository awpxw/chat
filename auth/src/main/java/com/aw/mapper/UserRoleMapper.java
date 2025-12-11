package com.aw.mapper;

import com.aw.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserRoleMapper {

    Integer insertBatch(@Param("userRoles") List<UserRole> userRoles,
                        @Param("loginUserId") Long loginUserId);

}
