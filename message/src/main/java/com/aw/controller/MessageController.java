package com.aw.controller;

import com.aw.dto.*;
import com.aw.exception.Result;
import com.aw.service.MessageService;
import com.aw.vo.AnnouncementVO;
import com.aw.vo.GlobalSearchVO;
import com.aw.vo.MessagePullVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/message")
public class MessageController {

    @Resource
    private MessageService messageService;

    @PostMapping("/pull")
    @Schema(description = "拉取消息")
    public Result<MessagePullVO> pull(@RequestBody MessagePullDTO dto) throws IOException {
        MessagePullVO vo = messageService.pull(dto);
        return Result.success(vo);
    }

    @PostMapping("/forward")
    @Schema(description = "转发消息（单条或多条）")
    public Result<String> forward(@RequestBody ForwardMsgDTO dto) throws IOException {
        messageService.forward(dto);
        return Result.success();
    }

    @PostMapping("/reply")
    @Schema(description = "回复指定消息（带引用）")
    public Result<String> reply(@RequestBody MsgReplyDTO dto) throws IOException {
        messageService.reply(dto);
        return Result.success();
    }

    @PostMapping("/global/search")
    @Schema(description = "全局消息搜索（关键词）")
    public Result<GlobalSearchVO> globalSearch(@RequestBody GlobalSearchDTO dto) {
        GlobalSearchVO globalSearchVO = messageService.globalSearch(dto);
        return Result.success(globalSearchVO);
    }

    @PostMapping("/reaction")
    @Schema(description = "消息表情回应（点赞、爱等等）")
    public void reaction() {

    }

    @PostMapping("/announcement")
    @Schema(description = "群公告")
    public Result<String> announcement(ConversationDTO dto) {
        messageService.announcement(dto);
        return Result.success();
    }

}
