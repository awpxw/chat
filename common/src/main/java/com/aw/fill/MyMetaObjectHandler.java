package com.aw.fill;

import com.aw.login.LoginUserInfo;
import com.aw.login.UserContext;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "deleted", Integer.class, 0);
        LoginUserInfo user = UserContext.get();
        if (user != null && user.getUserId() != null) {
            this.strictInsertFill(metaObject, "createUser", Long.class, user.getUserId());
        }
        if (user != null && user.getUsername()!= null) {
            this.strictInsertFill(metaObject, "createUserName", String.class, user.getUsername());
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        Long userId = UserContext.get().getUserId();
        String userName = UserContext.get().getUsername();
        if (userId != null) {
            this.strictUpdateFill(metaObject, "updateUser", Long.class, userId);
        }
        if (userId != null) {
            this.strictUpdateFill(metaObject, "updateUserName", String.class, userName);
        }
    }

}