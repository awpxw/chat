package com.aw.service.impl;


import com.aw.dto.MenuDTO;
import com.aw.entity.Menu;
import com.aw.exception.BizException;
import com.aw.mapper.MenuMapper;
import com.aw.redis.RedisUtils;
import com.aw.service.MenuService;
import com.aw.utils.tree.TreeUtil;
import com.aw.vo.MenuTreeResultVO;
import com.aw.vo.MenuTreeVO;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class MenuServiceImpl implements MenuService {

    @Resource
    private MenuMapper menuMapper;

    @Resource
    private RedisUtils redisUtils;

    @Override
    public MenuTreeResultVO tree() {
        String key = redisUtils.key("menuTree");
        return redisUtils.get(key, this::loadMenuTreeFromDB, 30L, TimeUnit.MINUTES, MenuTreeResultVO.class);
    }

    private MenuTreeResultVO loadMenuTreeFromDB() {
        List<Menu> menus = ChainWrappers.lambdaQueryChain(Menu.class)
                .orderByAsc(Menu::getSort)
                .orderByAsc(Menu::getId)
                .list();
        List<MenuTreeVO> menuTreeVOs = menus.stream()
                .map(menu -> {
                    MenuTreeVO menuTreeVO = new MenuTreeVO();
                    BeanUtils.copyProperties(menu, menuTreeVO);
                    return menuTreeVO;
                }).toList();
        List<MenuTreeVO> menuTree = TreeUtil.buildTree(menuTreeVOs, 0L);
        MenuTreeResultVO tree = new MenuTreeResultVO();
        tree.setMenuTree(menuTree);
        return tree;
    }

    @Override
    public void update(MenuDTO menuDTO) {

        updateDB(menuDTO);

        deleteCache();

    }

    @Override
    public void delete(MenuDTO menuDTO) {

        List<Long> ids = selectAllMenuNodes(menuDTO);

        removeIds(ids);

        deleteCache();

    }

    private void removeIds(List<Long> ids) {
        ChainWrappers.lambdaUpdateChain(Menu.class)
                .in(Menu::getId, ids)
                .remove();
    }

    private List<Long> selectAllMenuNodes(MenuDTO menuDTO) {
        Long rootId = menuDTO.getId();
        return menuMapper.selectAllChildIdsIncludeSelf(rootId);
    }

    private void deleteCache() {
        String key = redisUtils.key("menu");
        redisUtils.delete(key);
    }

    private void updateDB(MenuDTO menuDTO) {
        boolean success = menuMapper.saveOrUpdate(menuDTO) > 0;
        if (!success) {
            throw new BizException("【新增/修改】菜单失败");
        }
    }

}