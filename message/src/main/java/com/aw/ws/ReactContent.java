package com.aw.ws;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReactContent {

    /**
     * 表情反应列表
     */
    List<ReactDetail> reactDetails;

    @Data
    @Builder
    public static class ReactDetail{

        /**
         * 消息id
         */
        private Long messageId;

        /**
         * 表情id
         */
        private Integer emoji;

        /**
         * 表情计数
         */
        private Long count;

    }

}
