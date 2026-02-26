package com.aw.controller;

import com.aw.dto.ConversationDTO;
import com.aw.dto.group.ConversationCreate;
import com.aw.dto.group.ConversationUnreadTotal;
import com.aw.exception.Result;
import com.aw.service.ConversationService;
import com.aw.validate.ValidatorUtil;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/conversation")
public class ConversationController {

    @Resource
    private ConversationService conversationService;

    @PostMapping("/create")
    public Result<Long> create(@RequestBody ConversationDTO dto) {
        ValidatorUtil.validate(dto, ConversationCreate.class);
        Long conversationId = conversationService.create(dto);
        return Result.success(conversationId);
    }

    @PostMapping("/unread/total")
    public Result<Integer> unreadTotal(@RequestBody ConversationDTO dto) {
        ValidatorUtil.validate(dto, ConversationUnreadTotal.class);
        Integer unreadTotal = conversationService.unreadTotal(dto);
        return Result.success(unreadTotal);
    }

}
