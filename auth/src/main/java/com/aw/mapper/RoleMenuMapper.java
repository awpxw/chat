package com.aw.mapper;

import com.aw.entity.RoleMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色权限 Mapper 接口
 */
@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

    Integer insertBatch(@Param("roleMenus") List<RoleMenu> roleMenus,
                        @Param("userId") Long userId);

}
