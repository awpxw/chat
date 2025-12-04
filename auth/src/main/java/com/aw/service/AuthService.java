package com.aw.service;

import com.aw.dto.CaptchaDTO;
import com.aw.dto.DeptDTO;
import com.aw.dto.LoginDTO;
import com.aw.dto.UserDTO;
import com.aw.entity.User;
import com.aw.vo.CaptchaVO;
import com.aw.vo.DeptVO;
import com.aw.vo.LoginVO;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface AuthService {

    LoginVO login(LoginDTO loginDTO);

    void register(LoginDTO loginDTO);

    LoginVO refresh(LoginDTO loginVO);

    LoginVO logout(LoginDTO loginDTO);

    CaptchaVO captcha(CaptchaDTO captchaDTO);

    void captchaVerify(CaptchaDTO captchaDTO);

    void passwordChange(LoginDTO loginDTO);

    DeptVO loadDeptTreeFromDB();

    DeptVO deptTree();

    void updateDept(DeptDTO deptDTO);

    void deleteDept(DeptDTO deptDTO);

    void userAdd(UserDTO userDTO);

    void userUpdate(UserDTO userDTO);

    void userDelete(UserDTO userDTO);

    IPage<User> userPage(UserDTO userDTO);
}
