package com.aw.mapper;

import com.aw.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 员工表 Mapper 接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}