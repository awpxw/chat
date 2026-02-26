package com.aw.service.impl;

import com.aw.jwt.JwtUtil;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)  // 加这
class JwtUtilsTest {

    @Resource
    private JwtUtil jwtUtil;

    @Test
    void generate_access_token_success() {
        String token = jwtUtil.generateAccessToken(1L, "admin", null);
        System.out.println("生成的 token: " + token);
        assertNotNull(token);
    }

    @Test
    void generate_refresh_token_success() {
        String token = jwtUtil.generateRefreshToken(1L);
        System.out.println("生成的 token: " + token);
        assertNotNull(token);
    }

    @Test
    void validate_access_token_success() {
        boolean isSuccess =
                jwtUtil.validateToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzY0MzExMzE3LCJleHAiOjE3NjQzMTg1MTcsInVzZXJuYW1lIjoiYWRtaW4ifQ.nqytGq2aPzr8xovPLvvgAUv9gW5JK-Um17JrKyhlm2Y");
        assertTrue(isSuccess);    }

    @Test
    void validate_refresh_token_success() {
        boolean isSuccess =
                jwtUtil.validateToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzY0MzExMjI5LCJleHAiOjE3NjQ5MTYwMjl9.ze13VWaPDmRvUsoKvK0FXHY7lyluhaETRwKI733_sQI");
        assertTrue(isSuccess);
    }

}
