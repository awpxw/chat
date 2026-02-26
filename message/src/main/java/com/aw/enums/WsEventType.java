package com.aw.enums;

import lombok.Getter;

/**
 * WebSocket 事件类型枚举
 * 所有推送事件统一用这个枚举，便于前后端对齐和维护
 */
@Getter
public enum WsEventType {

    // ──────────────── 消息相关 ────────────────
    MESSAGE_NEW("message.new", "新消息推送"),
    MESSAGE_EDIT("message.edit", "消息被编辑"),
    MESSAGE_DELETE("message.delete", "消息被删除（撤回/删除）"),
    MESSAGE_RECALL("message.recall", "消息被撤回（发送者操作）"),

    // ──────────────── 消息反应（表情回应/点赞） ────────────────
    REACTION_ADD("reaction.add", "新增表情反应"),
    REACTION_UPDATE("reaction.update", "表情反应计数更新（推荐用这个广播）"),
    REACTION_REMOVE("reaction.remove", "移除表情反应"),

    // ──────────────── 会话（聊天窗口）相关 ────────────────
    CONVERSATION_CREATED("conversation.created", "新会话创建（被拉群、新单聊等）"),
    CONVERSATION_UPDATED("conversation.updated", "会话信息变更（名称、头像、置顶、免打扰、未读数等）"),
    CONVERSATION_DELETED("conversation.deleted", "会话被删除/退出"),
    CONVERSATION_ARCHIVED("conversation.archived", "会话被归档"),
    CONVERSATION_UNARCHIVED("conversation.unarchived", "会话取消归档"),
    CONVERSATION_PINNED("conversation.pinned", "会话置顶变更"),
    CONVERSATION_MUTE("conversation.mute", "会话免打扰变更"),

    // ──────────────── 已读/送达回执 ────────────────
    READ_RECEIPT("read.receipt", "已读回执"),
    DELIVERED_RECEIPT("delivered.receipt", "已送达回执"),

    // ──────────────── 用户状态/在线相关 ────────────────
    USER_ONLINE("user.online", "用户上线"),
    USER_OFFLINE("user.offline", "用户下线"),
    USER_TYPING("user.typing", "对方正在输入..."),
    USER_TYPING_STOP("user.typing.stop", "对方停止输入"),

    // ──────────────── 群聊/成员相关 ────────────────
    GROUP_MEMBER_JOIN("group.member.join", "成员加入群聊"),
    GROUP_MEMBER_LEAVE("group.member.leave", "成员退出群聊"),
    GROUP_MEMBER_KICKED("group.member.kicked", "成员被踢出群聊"),
    GROUP_MEMBER_ROLE_CHANGED("group.member.role.changed", "成员角色变更（管理员/普通）"),
    GROUP_NAME_CHANGED("group.name.changed", "群名称变更"),
    GROUP_AVATAR_CHANGED("group.avatar.changed", "群头像变更"),

    // ──────────────── 其他系统事件 ────────────────
    NOTIFICATION("notification", "系统通知/公告"),
    ERROR("error", "服务器推送错误信息（比如 token 过期）"),
    HEARTBEAT("heartbeat", "心跳响应（可选，用于长连接保活）"),

    // ──────────────── 扩展预留 ────────────────
    CUSTOM("custom", "自定义事件（业务扩展用）");

    private final String code;
    private final String description;

    WsEventType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据 code 反查枚举（前端传 code 时常用）
     */
    public static WsEventType fromCode(String code) {
        if (code == null) return null;
        for (WsEventType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null; // 或抛异常，根据项目规范
    }
}