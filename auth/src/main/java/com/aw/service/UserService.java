package com.aw.service;

import com.aw.dto.UserDTO;
import com.aw.entity.User;
import com.aw.vo.MenuTreeResultVO;
import com.aw.vo.UserPageVO;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface UserService {

    void add(UserDTO userDTO);

    void update(UserDTO userDTO);

    void delete(UserDTO userDTO);

    IPage<UserPageVO> page(UserDTO userDTO);

    void ban(UserDTO userDTO);

    void allotRole(UserDTO userDTO);

    MenuTreeResultVO menuTree(UserDTO userDTO);

    void kick(UserDTO userDTO);
}
