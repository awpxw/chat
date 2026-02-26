package com.aw.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息表情回应（Reaction）枚举
 * 包含最常用的 8 个表情，按流行度排序
 */
@Getter
public enum MessageReactionEmoji {

    THUMBS_UP("👍", "点赞", 1),
    HEART("❤️", "爱心", 2),
    LAUGHING("😂", "笑哭", 3),
    SURPRISED("😮", "惊讶", 4),
    SAD("😢", "难过", 5),
    ANGRY("😡", "生气", 6),
    FIRE("🔥", "火爆/厉害", 7),
    LIKE_ROCKET("🚀", "火箭/冲", 8);

    private final String emoji;
    private final String description;
    private final int order;  // 用于前端排序显示

    MessageReactionEmoji(String emoji, String description, int order) {
        this.emoji = emoji;
        this.description = description;
        this.order = order;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getDescription() {
        return description;
    }

    public int getOrder() {
        return order;
    }

    /**
     * 根据 emoji 字符串反查枚举
     */
    public static MessageReactionEmoji fromEmoji(String emoji) {
        if (emoji == null) return null;
        for (MessageReactionEmoji e : values()) {
            if (e.emoji.equals(emoji)) {
                return e;
            }
        }
        return null;  // 或抛异常 / 返回默认
    }

    /**
     * 获取所有支持的表情列表（前端下拉/面板用）
     */
    public static List<MessageReactionEmoji> getAll() {
        return Arrays.stream(values())
                     .sorted(Comparator.comparingInt(MessageReactionEmoji::getOrder))
                     .collect(Collectors.toList());
    }

    public static int getSize(){
        return getAll().size();
    }

}