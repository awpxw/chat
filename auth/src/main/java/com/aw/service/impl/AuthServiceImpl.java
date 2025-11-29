package com.aw.service.impl;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.aw.dto.CaptchaDTO;
import com.aw.dto.LoginDTO;
import com.aw.entity.BannedUser;
import com.aw.entity.User;
import com.aw.jwt.JwtUtil;
import com.aw.mapper.BannedUserMapper;
import com.aw.mapper.UserMapper;
import com.aw.service.AuthService;
import com.aw.exception.BizException;
import com.aw.utils.CaptchaUtils;
import com.aw.vo.CaptchaVO;
import com.aw.vo.LoginVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private CaptchaUtils captchaUtils;

    @Resource
    private BannedUserMapper bannedUserMapper;

    @Resource
    private JwtUtil jwtUtil;

    @Override
    public LoginVO login(LoginDTO loginDTO) {

        checkCaptchaNull(loginDTO);

        checkCaptchaExpireOrWrong(loginDTO);

        checkNameAndPassword(loginDTO);

        checkAccountBanned(loginDTO);

        String accessToken = generateAccessToken(loginDTO);

        String refreshToken = generateRefreshToken(loginDTO);

        LoginVO loginVO = new LoginVO();

        loginVO.setAccessToken(accessToken);

        loginVO.setRefreshToken(refreshToken);

        return loginVO;
    }

    @Override
    public void register(LoginDTO loginDTO) {

        checkNameRepeat(loginDTO);

        checkPhoneRepeat(loginDTO);

        checkCaptchaExpireOrWrong(loginDTO);

        addNewUser(loginDTO);

    }

    @Override
    public LoginVO refresh(LoginDTO loginDTO) {

        checkInvalid(loginDTO);

        Claims claims = jwtUtil.parseToken(loginDTO.getRefreshToken());

        User user = findUser(claims.getSubject());

        String accessToken = generateAccessTokenByUserInfo(user);

        LoginVO loginVO = new LoginVO();

        loginVO.setAccessToken(accessToken);

        return loginVO;

    }

    @Override
    public LoginVO logout(LoginDTO loginDTO) {
        return null;
    }

    @Override
    public CaptchaVO captcha(CaptchaDTO captchaDTO) {

        Integer expireIns = captchaDTO.getExpireIns();

        Map<String, String> map = generateCaptcha(expireIns);

        CaptchaVO captchaVO = new CaptchaVO();

        captchaVO.setCaptchaId(map.get("captchaId"));

        captchaVO.setImage(map.get("image"));

        return captchaVO;

    }

    @Override
    public void captchaVerify(CaptchaDTO captchaDTO) {
        String uuid = captchaDTO.getUuid();
        String code = captchaDTO.getCode();
        if (!captchaUtils.validate(uuid, code)) {
            throw new BizException("验证码失效");
        }
    }

    private Map<String, String> generateCaptcha(int expireIns) {
        return captchaUtils.generateCaptcha(expireIns);
    }

    private String generateAccessTokenByUserInfo(User user) {
        return jwtUtil.generateAccessToken(user.getId(), user.getName(), null);
    }

    private User findUser(String id) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getId, Long.parseLong(id)));
    }

    private void checkInvalid(LoginDTO loginDTO) {
        String accessToken = loginDTO.getRefreshToken();
        boolean isValid = jwtUtil.validateToken(accessToken);
        if (!isValid) {
            throw new BizException("验证码过期或错误");
        }
    }

    private void addNewUser(LoginDTO loginDTO) {
        User user = new User();
        BeanUtils.copyProperties(loginDTO, user);
        user.setName(loginDTO.getUsername());
        userMapper.insert(user);
    }

    private void checkPhoneRepeat(LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String mobile = loginDTO.getMobile();
        LambdaUpdateWrapper<User> wrapper =
                new LambdaUpdateWrapper<User>()
                        .eq(User::getName, username)
                        .eq(User::getMobile, mobile);
        List<User> users = userMapper.selectList(wrapper);
        if (!CollectionUtils.isEmpty(users)) {
            throw new BizException("手机号重复");
        }
    }

    private void checkNameRepeat(LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        List<User> users = userMapper.selectList(new LambdaQueryWrapper<User>().eq(User::getName, username));
        if (CollectionUtils.isNotEmpty(users)) {
            throw new BizException("用户名重复");
        }
    }

    private String generateAccessToken(LoginDTO loginDTO) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<User>().eq(User::getName, loginDTO.getUsername());
        User user = userMapper.selectOne(wrapper);
        Long userId = user.getId();
        String userName = user.getName();
        return jwtUtil.generateAccessToken(userId, userName, null);
    }

    private String generateRefreshToken(LoginDTO loginDTO) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<User>().eq(User::getName, loginDTO.getUsername());
        User user = userMapper.selectOne(wrapper);
        Long userId = user.getId();
        return jwtUtil.generateRefreshToken(userId);
    }

    private void checkAccountBanned(LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getName, username)
                .eq(User::getPassword, password));
        Long id = user.getId();
        List<BannedUser> bannedUsers = bannedUserMapper.selectList(new LambdaUpdateWrapper<BannedUser>().eq(BannedUser::getUserId, id));
        if (Objects.nonNull(bannedUsers) && !bannedUsers.isEmpty()) {
            throw new BizException("账号已被禁用");
        }
    }

    private void checkCaptchaExpireOrWrong(LoginDTO loginDTO) {
        String captcha = loginDTO.getCaptcha();
        String captchaId = loginDTO.getCaptchaId();
        try {
            boolean validate = captchaUtils.validate(captcha, captchaId);
            if (!validate) {
                throw new BizException("验证码过期或错误");
            }
        } catch (Exception e) {
            throw new BizException("验证码过期或错误");
        }
    }

    private void checkCaptchaNull(LoginDTO loginDTO) {
        if (Objects.isNull(loginDTO)
                || Objects.isNull(loginDTO.getCaptcha())
                || Objects.isNull(loginDTO.getCaptchaId())) {
            throw new BizException("验证码不能为空");
        }
    }

    private void checkNameAndPassword(LoginDTO loginDTO) {
        try {
            if (Objects.nonNull(loginDTO)
                    && Objects.nonNull(loginDTO.getUsername())
                    && Objects.nonNull(loginDTO.getPassword())) {
                LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                        .eq(User::getName, loginDTO.getUsername())
                        .eq(User::getPassword, loginDTO.getPassword());
                User user = userMapper.selectOne(wrapper);
                if (Objects.nonNull(user)) {
                    log.info("success");
                } else {
                    throw new BizException("账号或密码错误");
                }
            } else {
                throw new BizException("账号或密码错误");
            }
        } catch (Exception e) {
            throw new BizException("账号或密码错误");
        }
    }

}
