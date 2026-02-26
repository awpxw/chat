package com.aw.ws;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReplyContent {

    /**
     * 是否群聊
     */
    private Boolean isGroup;

    /**
     * 发送人
     */
    private Long fromId;

    /**
     * 接收人
     */
    private Long toId;

    /**
     * 回复内容
     */
    private ReplyDetail replyDetail;

    @Data
    @Builder
    public static class ReplyDetail {

        /**
         * 发送消息
         */
        private String originMsg;

        /**
         * 被引用消息
         */
        private String referenceMsg;

    }

    public static ReplyContent replyContent(Boolean isGroup, Long fromId, Long toId, String msg, String referenceMsg) {
        ReplyDetail detail = new ReplyDetail(msg, referenceMsg);
        return ReplyContent.builder()
                .isGroup(isGroup)
                .fromId(fromId)
                .toId(toId)
                .replyDetail(detail)
                .build();
    }

}
