package com.aw.controller;

import com.aw.dto.MenuDTO;
import com.aw.dto.groups.MenuAddGroup;
import com.aw.dto.groups.MenuDeleteGroup;
import com.aw.dto.groups.MenuUpdateGroup;
import com.aw.exception.Result;
import com.aw.service.MenuService;
import com.aw.validate.ValidatorUtil;
import com.aw.vo.MenuTreeResultVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/menu")
public class MenuController {

    @Resource
    private MenuService menuService;

    @PostMapping("/tree")
    public Result<MenuTreeResultVO> tree() {
        MenuTreeResultVO tree = menuService.tree();
        return Result.success(tree);
    }

    @PostMapping("/update")
    public Result<String> update(@RequestBody MenuDTO menuDTO) {
        ValidatorUtil.validate(menuDTO, menuDTO.getId() == null ? MenuUpdateGroup.class : MenuAddGroup.class);
        menuService.update(menuDTO);
        return Result.success();
    }

    @PostMapping("/delete")
    public Result<String> delete(@RequestBody MenuDTO menuDTO) {
        ValidatorUtil.validate(menuDTO, MenuDeleteGroup.class);
        menuService.delete(menuDTO);
        return Result.success();
    }


}
