package com.aw.service;

import com.aw.dto.MemberDTO;

import java.util.List;

public interface MemberService {

    /**
     * 获取成员列表
     */
    List<Long> list(MemberDTO memberDTO);

    /**
     * 会话添加成员
     */
    void add(MemberDTO memberDTO);

    /**
     * 移除群聊
     */
    void remove(MemberDTO memberDTO);

    /**
     * 设置权限
     */
    void role(MemberDTO memberDTO);

}
