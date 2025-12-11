package com.aw.service;

import com.aw.dto.CaptchaDTO;
import com.aw.dto.LoginDTO;
import com.aw.vo.CaptchaVO;
import com.aw.vo.LoginVO;

public interface LoginService {

    LoginVO login(LoginDTO loginDTO);

    void register(LoginDTO loginDTO);

    LoginVO refresh(LoginDTO loginVO);

    void logout(LoginDTO loginDTO);

    CaptchaVO captcha(CaptchaDTO captchaDTO);

    void captchaVerify(CaptchaDTO captchaDTO);

    void passwordChange(LoginDTO loginDTO);

}
