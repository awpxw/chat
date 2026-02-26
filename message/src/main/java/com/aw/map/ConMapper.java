package com.aw.map;

import com.aw.entity.Conversation;
import com.aw.vo.ConversationVO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConMapper {

    ConversationVO.ConversationDetail toDetail(Conversation conversation);

    List<ConversationVO.ConversationDetail> toDetailList(List<Conversation> conversations);

}