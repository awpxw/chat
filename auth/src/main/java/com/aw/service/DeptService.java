package com.aw.service;// DeptService.java

import com.aw.dto.DeptDTO;
import com.aw.entity.Dept;
import com.aw.vo.DeptVO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface DeptService {

    DeptVO loadDeptTreeFromDB();

    DeptVO tree();

    void update(DeptDTO deptDTO);

    void delete(DeptDTO deptDTO);

}