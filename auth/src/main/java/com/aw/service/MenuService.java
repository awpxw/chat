package com.aw.service;


import com.aw.dto.MenuDTO;
import com.aw.vo.MenuTreeResultVO;

public interface MenuService {

    MenuTreeResultVO tree();

    void update(MenuDTO menuDTO);

    void delete(MenuDTO menuDTO);

}

