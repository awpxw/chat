package com.aw.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ConversationVO {

    /**
     * 会话列表
     */
    private List<ConversationDetail> conversationDetails;

    @Data
    @Builder
    public static class ConversationDetail {
        /**
         * 会话类型：1单聊 2群聊 3客服会话
         */
        private Integer type;

        /**
         * 会话名称（群聊必填，单聊可选为对方备注名）
         */
        private String name;

        /**
         * 会话头像URL
         */
        private String avatar;

        /**
         * 命中的关键词
         */
        private String keyWord;

    }

}
