package com.aw.mapper;

import com.aw.dto.UserDTO;
import com.aw.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 员工表 Mapper 接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 条件分页查询（XML 实现）
     */
    IPage<User> selectUserPage(Page<User> page, @Param("dto") UserDTO userDTO);

}