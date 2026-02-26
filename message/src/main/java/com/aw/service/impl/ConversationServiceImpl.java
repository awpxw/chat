package com.aw.service.impl;

import com.aw.dto.ConversationDTO;
import com.aw.entity.Conversation;
import com.aw.entity.ConversationMember;
import com.aw.exception.BizException;
import com.aw.login.UserContext;
import com.aw.map.ConMapper;
import com.aw.mapper.ConversationMapper;
import com.aw.service.ConversationService;
import com.aw.utils.HighlightUtil;
import com.aw.vo.ConversationVO;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class ConversationServiceImpl implements ConversationService {

    @Resource
    private ConversationMapper conversationMapper;

    @Resource
    private ConMapper conMapper;

    @Override
    public Long create(ConversationDTO dto) {
        String name = Strings.join(dto.getInitialUserName(), ',');
        Long creatorId = UserContext.get().getUserId();
        Conversation conversation = Conversation.builder()
                .creatorId(creatorId)
                .name(name)
                .type(dto.getType())
                .isPinned(true)
                .mute(false)
                .build();
        int insert = conversationMapper.insert(conversation);
        if (insert != 1) {
            log.error(">>>创建会话失败");
            throw new BizException("创建会话失败");
        }
        return conversation.getId();
    }

    @Override
    public ConversationVO search(ConversationDTO dto) {

        List<ConversationVO.ConversationDetail> byNames = searchByName(dto);

        List<ConversationVO.ConversationDetail> highLightName = highLightByName(byNames, dto.getMemberOrName());

        List<ConversationVO.ConversationDetail> byByMemberName = searchByMemberName(dto);

        List<ConversationVO.ConversationDetail> highLightMember = highLightByMember(byByMemberName, dto.getMemberOrName());

        return mergeResult(highLightName, highLightMember);

    }

    private List<ConversationVO.ConversationDetail> highLightByMember(List<ConversationVO.ConversationDetail> byMembers, String memberOrName) {
        return byMembers.stream().peek(t -> {
            t.setKeyWord("成员："+ memberOrName);
            String keyword = HighlightUtil.highlightKeyword(t.getKeyWord(), memberOrName);
            t.setKeyWord(keyword);
        }).toList();
    }

    private List<ConversationVO.ConversationDetail> highLightByName(List<ConversationVO.ConversationDetail> byNames, String memberOrName) {
        return byNames.stream().peek(t -> {
            String name = HighlightUtil.highlightKeyword(t.getName(), memberOrName);
            t.setName(name);
        }).toList();
    }

    private ConversationVO mergeResult(List<ConversationVO.ConversationDetail> byNames, List<ConversationVO.ConversationDetail> byByMemberName) {
        List<ConversationVO.ConversationDetail> result = new ArrayList<>();
        if (!CollectionUtils.isEmpty(byNames)) {
            result.addAll(byNames);
        }
        if (!CollectionUtils.isEmpty(byByMemberName)) {
            result.addAll(byByMemberName);
        }
        return ConversationVO.builder()
                .conversationDetails(result)
                .build();
    }

    private List<ConversationVO.ConversationDetail> searchByMemberName(ConversationDTO dto) {
        String name = dto.getMemberOrName();
        //群成员
        List<Long> conversationIds = ChainWrappers.lambdaQueryChain(ConversationMember.class)
                .like(ConversationMember::getUserName, name)
                .last("limit 100")
                .list()
                .stream()
                .map(ConversationMember::getConversationId)
                .toList();
        //群
        return ChainWrappers.lambdaQueryChain(Conversation.class)
                .in(Conversation::getId, conversationIds)
                .list()
                .stream()
                .map(conMapper::toDetail)
                .toList();
    }

    private List<ConversationVO.ConversationDetail> searchByName(ConversationDTO dto) {
        String name = dto.getMemberOrName();
        return ChainWrappers.lambdaQueryChain(Conversation.class)
                .eq(Conversation::getName, name)
                .last("limit 100")
                .list()
                .stream()
                .sorted(Comparator.comparing(Conversation::getCreateTime))
                .map(conMapper::toDetail)
                .toList();
    }

    @Override
    public Integer unreadTotal(ConversationDTO dto) {
        Long userId = dto.getUserId();
        Long conversationId = dto.getConversationId();
        ConversationMember member = ChainWrappers.lambdaQueryChain(ConversationMember.class)
                .eq(ConversationMember::getConversationId, conversationId)
                .eq(ConversationMember::getUserId, userId)
                .last("limit 1")
                .one();
        if (member == null) {
            log.error(">>>用户不存在，用户id：{}", userId);
            throw new BizException("用户不存在");
        }
        Integer unreadCount = member.getUnreadCount();
        return unreadCount == null ? 0 : unreadCount;
    }


}
