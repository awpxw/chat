package com.aw.controller;

import com.aw.dto.DeptDTO;
import com.aw.dto.groups.DeptAddGroup;
import com.aw.dto.groups.DeptDeleteGroup;
import com.aw.dto.groups.DeptUpdateGroup;
import com.aw.exception.Result;
import com.aw.login.LoginRequired;
import com.aw.service.DeptService;
import com.aw.validate.ValidatorUtil;
import com.aw.vo.DeptVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/auth/dept")
public class DeptController {

    @Resource
    private DeptService deptService;

    @PostMapping("/tree")
    @LoginRequired
    public Result<DeptVO> tree() {
        DeptVO deptVO = deptService.tree();
        return Result.success(deptVO);
    }

    @PostMapping("/update")
    @LoginRequired
    public Result<DeptVO> update(@RequestBody DeptDTO deptDTO) {
        ValidatorUtil.validate(deptDTO, deptDTO.getId() == null ? DeptAddGroup.class : DeptUpdateGroup.class);
        deptService.update(deptDTO);
        return Result.success();
    }

    @PostMapping("/delete")
    @LoginRequired
    public Result<DeptVO> delete(@RequestBody DeptDTO deptDTO) {
        ValidatorUtil.validate(deptDTO, DeptDeleteGroup.class);
        deptService.delete(deptDTO);
        return Result.success();
    }

}
