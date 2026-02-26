package com.aw.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class MessagePullVO {

    private List<MessageDetail> messageDetails;

    @Data
    @Builder
    public static class MessageDetail {

        /**
         * 发送者ID
         */
        private Long senderId;

        /**
         * 消息内容
         */
        private String content;

        /**
         * 消息发送时间
         */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime msgTime;

    }

}
