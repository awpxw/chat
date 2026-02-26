package com.aw.mapper;

import com.aw.entity.Message;
import com.aw.login.LoginUserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MessageMapper extends BaseMapper<Message> {

    void batchInsert(@Param("list") List<Message> list,
                     @Param("user")LoginUserInfo user);
}
