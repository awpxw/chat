package com.aw.map;

import com.aw.entity.Message;
import com.aw.vo.MessagePullVO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MsgMapper {

    MessagePullVO.MessageDetail toDetail(Message message);

}
