package com.aw.mapper;

import com.aw.entity.Dept;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DeptMapper extends BaseMapper<Dept> {

    /** 递归逻辑删除 */
    int logicDeleteWithChildren(@Param("deptId") Long deptId);

}