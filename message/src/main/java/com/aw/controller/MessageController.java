package com.aw.controller;

import com.aw.dto.ForwardMsgDTO;
import com.aw.exception.Result;
import com.aw.service.MessageService;
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

    @PostMapping("/forward")
    @Schema(description = "转发消息（单条或多条）")
    public Result<String> forward(@RequestBody ForwardMsgDTO dto) throws IOException {
        messageService.forward(dto);
        return Result.success();
    }

    @PostMapping("/reply/{msgId}")
    @Schema(description = "回复指定消息（带引用）")
    public void reply() {

    }

    @PostMapping("/global/search")
    @Schema(description = "全局消息搜索（关键词）")
    public void globalSearch() {

    }
    @PostMapping("/search")
    @Schema(description = "搜索会话（按名称/成员）")
    public void search() {

    }

    @PostMapping("/reaction")
    @Schema(description = "消息表情回应（点赞、爱等等）")
    public void reaction() {

    }
    @PostMapping("/archive")
    @Schema(description = "归档会话（隐藏但保留）")
    public void archive() {

    }
    @PostMapping("/announcement")
    @Schema(description = "群公告")
    public void announcement() {

    }

    @PostMapping("/quit-notification")
    @Schema(description = "成员退出群通知")
    public void quitNotification() {

    }

}
