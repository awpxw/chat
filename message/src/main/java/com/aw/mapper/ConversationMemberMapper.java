package com.aw.mapper;

import com.aw.entity.ConversationMember;
import com.aw.login.LoginUserInfo;
import com.aw.login.UserContext;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
public interface ConversationMemberMapper extends BaseMapper<ConversationMember> {

    void saveBatch(@Param("members") List<ConversationMember> conversationMembers,
                   @Param("user") LoginUserInfo user);

}
