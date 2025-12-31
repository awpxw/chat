package com.aw.controller;

import com.aw.dto.MemberDTO;
import com.aw.dto.group.MemberAdd;
import com.aw.dto.group.MemberList;
import com.aw.dto.group.MemberRemove;
import com.aw.dto.group.MemberRole;
import com.aw.exception.Result;
import com.aw.service.MemberService;
import com.aw.validate.ValidatorUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/member")
public class MemberController {

    @Resource
    private MemberService memberService;

    @PostMapping("/add")
    public Result<String> add(@RequestBody MemberDTO memberDTO) {
        ValidatorUtil.validate(memberDTO, MemberAdd.class);
        memberService.add(memberDTO);
        return Result.success();
    }

    @PostMapping("/remove")
    public Result<String> remove(@RequestBody MemberDTO memberDTO) {
        ValidatorUtil.validate(memberDTO, MemberRemove.class);
        memberService.remove(memberDTO);
        return Result.success();
    }

    @PostMapping("/role")
    public Result<String> role(@RequestBody MemberDTO memberDTO) {
        ValidatorUtil.validate(memberDTO, MemberRole.class);
        memberService.role(memberDTO);
        return Result.success();
    }

    @PostMapping("/list")
    public Result<List<Long>> list(@RequestBody MemberDTO memberDTO) {
        ValidatorUtil.validate(memberDTO, MemberList.class);
        List<Long> MemberIds = memberService.list(memberDTO);
        return Result.success(MemberIds);
    }

}
