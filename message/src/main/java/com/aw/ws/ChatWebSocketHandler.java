package com.aw.ws;

import cn.hutool.json.JSONUtil;
import com.aw.dto.MemberDTO;
import com.aw.dto.MessageDTO;
import com.aw.entity.Message;
import com.aw.jwt.JwtUtil;
import com.aw.service.MemberService;
import com.aw.service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private MessageService messageService;

    @Resource
    private MemberService memberService;

    private final ConcurrentMap<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) {
        Long userId = parseUserFromHeader(session);
        sessions.put(userId, session);
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) {
        Long userId = parseUserFromHeader(session);
        sessions.remove(userId);
    }

    @Override
    protected void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) {
        try {

            MessageDTO dto = saveMessage(message);

            deliveryMessage(message, dto);

        } catch (Exception e) {
            log.error(">>>消息发送失败:{}", e.getMessage());
        }
    }

    private void deliveryMessage(@NotNull TextMessage message, MessageDTO dto) throws IOException {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setConversionId(dto.getConversationId());
        List<Long> memberIds = memberService.list(memberDTO);
        for (Long memberId : memberIds) {
            WebSocketSession targetSession = sessions.get(memberId);
            if (targetSession != null && targetSession.isOpen()) {
                targetSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(message.getPayload())));
            }
        }
    }

    private MessageDTO saveMessage(@NotNull TextMessage message) throws JsonProcessingException {
        MessageDTO dto = objectMapper.readValue(message.getPayload(), MessageDTO.class);
        messageService.saveMessage(dto);
        return dto;
    }

    private Long parseUserFromHeader(WebSocketSession session) {
        HttpHeaders headers = session.getHandshakeHeaders();
        String token = Objects.requireNonNull(headers.get("Authorization")).get(0);
        return jwtUtil.getUserId(token);
    }

    public void pushMessage(Message message, List<Long> userIds) throws IOException {
        List<WebSocketSession> onlineUserSession = new ArrayList<>();
        for (Long userId : userIds) {
            WebSocketSession session = sessions.get(userId);
            if (session != null && session.isOpen()) {
                //离线用户不推送，上线后自动拉取消息记录
                onlineUserSession.add(session);
            }
        }
        for (WebSocketSession session : onlineUserSession) {
            String msg = JSONUtil.toJsonStr(message);
            session.sendMessage(new TextMessage(msg));
        }
    }


}