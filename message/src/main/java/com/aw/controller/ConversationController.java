package com.aw.controller;

import com.aw.dto.ConversationDTO;
import com.aw.dto.group.ConversationCreate;
import com.aw.dto.group.ConversationSearch;
import com.aw.dto.group.ConversationUnreadTotal;
import com.aw.dto.group.QuitNotify;
import com.aw.exception.Result;
import com.aw.service.ConversationService;
import com.aw.validate.ValidatorUtil;
import com.aw.vo.ConversationVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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

    @PostMapping("/search")
    @Schema(description = "搜索会话（按名称/成员）")
    public Result<ConversationVO> search(@RequestBody ConversationDTO dto) {
        ValidatorUtil.validate(dto, ConversationSearch.class);
        ConversationVO conversationVO = conversationService.search(dto);
        return Result.success(conversationVO);
    }

    @PostMapping("/quitNotify")
    @Schema(description = "成员退出群通知")
    public Result<String> quitNotify(ConversationDTO dto) throws IOException {
        ValidatorUtil.validate(dto, QuitNotify.class);
        conversationService.quitNotify(dto);
        return Result.success();
    }

}
