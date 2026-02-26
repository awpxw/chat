package com.aw.job;

import com.aw.entity.Message;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class MessageJob {

    @Scheduled(fixedRate = 60000)
    public void archive() {
        log.info(">>>开始归档消息");
        List<Message> msg = ChainWrappers.lambdaQueryChain(Message.class)
                .eq(Message::getIsArchived, false)
                .eq(Message::getConversationId, null)
                .gt(Message::getCreateTime, LocalDateTime.now().minusDays(7))
                .last("limit 200")
                .list();
        log.info(">>>归档消息数量为：{}", msg);
        if (!CollectionUtils.isEmpty(msg)) {
            ChainWrappers.lambdaUpdateChain(Message.class)
                    .eq(Message::getIsArchived, false)
                    .eq(Message::getConversationId, null)
                    .in(Message::getId, msg)
                    .set(Message::getIsArchived, true)
                    .update();
        }
        log.info(">>>归档消息结束");
    }

}
