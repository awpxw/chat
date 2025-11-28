package com.aw.service;

import com.aw.dto.LoginDTO;
import com.aw.vo.LoginVO;

public interface AuthService {

    LoginVO login(LoginDTO loginDTO);

    void register(LoginDTO loginDTO);

    LoginVO refresh(LoginDTO loginVO);
}
