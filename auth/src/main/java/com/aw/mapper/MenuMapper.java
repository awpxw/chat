package com.aw.mapper;

import com.aw.dto.MenuDTO;
import com.aw.entity.Menu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    Integer saveOrUpdate(@Param("dto") MenuDTO menuDTO);

    List<Long> selectAllChildIdsIncludeSelf(@Param("rootId") Long rootId);

}