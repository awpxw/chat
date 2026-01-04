package com.aw.mapper;

import com.aw.entity.ForwardMsg;
import com.aw.login.LoginUserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ForwardMsgMapper extends BaseMapper<ForwardMsg> {

    /**
     * 批量插入转发消息记录
     */
    void batchInsert(@Param("list") List<ForwardMsg> list,
                     @Param("user") LoginUserInfo user);

    /**
     * 查找会话
     */
    Long findConversationByUserId(@Param("toUser") Long toUser,
                                  @Param("fromUser") Long fromUser);
}
