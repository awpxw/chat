package com.aw.controller;

import com.aw.dto.ConversationDTO;
import com.aw.dto.group.ConversationUnreadTotal;
import com.aw.exception.Result;
import com.aw.service.ConversationService;
import com.aw.validate.ValidatorUtil;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/conversation")
public class ConversationController {

    @Resource
    private ConversationService conversationService;

    @PostMapping("/unread/total")
    public Result<Integer> unreadTotal(@RequestBody ConversationDTO dto) {
        ValidatorUtil.validate(dto, ConversationUnreadTotal.class);
        Integer unreadTotal = conversationService.unreadTotal(dto);
        return Result.success(unreadTotal);
    }

}
