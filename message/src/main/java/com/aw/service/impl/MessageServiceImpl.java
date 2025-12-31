package com.aw.service.impl;

import com.aw.dto.MessageDTO;
import com.aw.entity.Message;
import com.aw.exception.BizException;
import com.aw.login.UserContext;
import com.aw.mapper.MessageMapper;
import com.aw.service.MessageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    @Resource
    private MessageMapper messageMapper;

    @Override
    public void saveMessage(MessageDTO dto) {
        Message msg = new Message();
        BeanUtils.copyProperties(dto, msg);
        msg.setSenderId(UserContext.get().getUserId());
        int success = messageMapper.insert(msg);
        if (success <= 0) {
            log.error(">>>消息发送失败:{}", dto.getContent());
            throw new BizException("消息发送失败");
        }
    }

}
