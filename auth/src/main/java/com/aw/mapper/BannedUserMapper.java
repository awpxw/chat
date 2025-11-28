package com.aw.mapper;

import com.aw.entity.BannedUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 禁用用户
 */
@Mapper
public interface BannedUserMapper extends BaseMapper<BannedUser> {
}