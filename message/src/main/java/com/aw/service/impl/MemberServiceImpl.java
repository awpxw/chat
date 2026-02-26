package com.aw.service.impl;

import com.aw.dto.MemberDTO;
import com.aw.entity.Conversation;
import com.aw.entity.ConversationMember;
import com.aw.enums.ConversationTypeEnum;
import com.aw.enums.MemberStatusEnum;
import com.aw.enums.RoleEnum;
import com.aw.exception.BizException;
import com.aw.login.UserContext;
import com.aw.mapper.ConversationMemberMapper;
import com.aw.service.MemberService;
import com.aw.snowflake.IdWorker;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class MemberServiceImpl implements MemberService {

    @Resource
    private ConversationMemberMapper conversationMemberMapper;

    @Resource
    private IdWorker idWorker;

    @Override
    public List<Long> list(MemberDTO memberDTO) {

        List<ConversationMember> members = checkIfMemberExist(memberDTO);

        return collectMemberIds(members);

    }

    @Override
    public void add(MemberDTO memberDTO) {

        List<ConversationMember> conversationMembers = dto2Member(memberDTO);

        batchSaveMembers(conversationMembers);

    }

    @Override
    public void remove(MemberDTO memberDTO) {

        checkIfGroupChat(memberDTO);

        removeMember(memberDTO);

    }

    @Override
    public void role(MemberDTO memberDTO) {

        checkIfCurrentStatusLegal(memberDTO);

        updateMemberRole(memberDTO);

    }

    private void updateMemberRole(MemberDTO memberDTO) {
        Long userId = memberDTO.getUserId();
        Integer memberRole = memberDTO.getMemberRole();
        Long conversionId = memberDTO.getConversionId();
        boolean success = ChainWrappers.lambdaUpdateChain(ConversationMember.class)
                .eq(ConversationMember::getUserId, userId)
                .eq(ConversationMember::getConversationId, conversionId)
                .set(ConversationMember::getRole, memberRole)
                .update();
        if (!success) {
            log.error(">>>设置用户权限失败，用户id：{}，状态：{}", userId, MemberStatusEnum.fromCode(memberRole));
            throw new BizException("设置用户权限失败");
        }
    }

    private void checkIfCurrentStatusLegal(MemberDTO memberDTO) {
        Long userId = memberDTO.getUserId();
        Integer memberRole = memberDTO.getMemberRole();
        Long conversionId = memberDTO.getConversionId();
        ConversationMember member = ChainWrappers.lambdaQueryChain(ConversationMember.class)
                .eq(ConversationMember::getUserId, userId)
                .eq(ConversationMember::getConversationId, conversionId)
                .last("limit 1")
                .one();
        if (member == null) {
            log.error(">>>成员不存在，会话id：{}，成员id：{}", conversionId, userId);
            throw new BizException("成员不存在");
        }
        Integer status = member.getStatus();
        if (Objects.equals(status, memberRole)) {
            throw new BizException("当前状态已为【" + MemberStatusEnum.fromCode(memberRole) + "】");
        }
        if (Objects.equals(status, MemberStatusEnum.EXITED.getCode())) {
            throw new BizException("当前成员已退出群聊");
        }
    }

    private void removeMember(MemberDTO memberDTO) {
        Long conversionId = memberDTO.getConversionId();
        Long userId = memberDTO.getUserId();
        boolean success = ChainWrappers.lambdaUpdateChain(ConversationMember.class)
                .eq(ConversationMember::getConversationId, conversionId)
                .eq(ConversationMember::getId, userId)
                .set(ConversationMember::getStatus, MemberStatusEnum.EXITED)
                .update();
        if (!success) {
            log.error(">>>移除成员失败，会话id：{}，成员id：{}", conversionId, userId);
            throw new BizException("移除成员失败");
        }

    }

    private void checkIfGroupChat(MemberDTO memberDTO) {
        Long conversionId = memberDTO.getConversionId();
        Conversation conversation = ChainWrappers.lambdaQueryChain(Conversation.class)
                .eq(Conversation::getId, conversionId)
                .eq(Conversation::getType, ConversationTypeEnum.GROUP.getCode())
                .last("limit 1")
                .one();
        if (conversation == null) {
            log.error(">>>群聊【{}】不存在", conversionId);
            throw new BizException("群聊不存在，请联系管理员");
        }
    }

    private void batchSaveMembers(List<ConversationMember> members) {
        if (CollectionUtils.isEmpty(members)) {
            return;
        }
        try {
            conversationMemberMapper.saveBatch(members, UserContext.get());
        } catch (Exception e) {
            log.error(">>>新增成员失败,成员ids：{}", members);
            throw new BizException("新增成员失败");
        }
    }

    private List<ConversationMember> dto2Member(MemberDTO memberDTO) {
        if (memberDTO == null) {
            return Collections.emptyList();
        }
        List<ConversationMember> conversationMembers = new ArrayList<>();
        for (Long userId : memberDTO.getUserIds()) {
            ConversationMember member = new ConversationMember();
            member.setId(idWorker.nextId());
            member.setConversationId(memberDTO.getConversionId());
            member.setUserId(userId);
            member.setRole(RoleEnum.NORMAL.getCode());
            conversationMembers.add(member);
        }
        return conversationMembers;
    }

    private List<Long> collectMemberIds(List<ConversationMember> members) {
        return members.stream().map(ConversationMember::getId).toList();
    }

    private List<ConversationMember> checkIfMemberExist(MemberDTO memberDTO) {
        List<ConversationMember> members = ChainWrappers.lambdaQueryChain(ConversationMember.class)
                .eq(ConversationMember::getConversationId, memberDTO.getConversionId())
                .list();
        if (CollectionUtils.isEmpty(members)) {
            return Collections.emptyList();
        }
        return members;
    }

}
