package com.example.toeicquiz.controller;

import com.example.toeicquiz.service.RoomRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RoomController {
    private final RoomRegistry registry;
    private final SimpMessagingTemplate template;

    record JoinPayload(String roomId, String user) {}
    record StartPayload(String roomId) {}
    record RoomEvent(String type, String user, String roomId, int count) {}

    @MessageMapping("/room/join")
    public void join(JoinPayload p) {
        String roomId = StringUtils.hasText(p.roomId()) ? p.roomId() : "ROOM1";
        String user   = StringUtils.hasText(p.user())   ? p.user()   : "guest";
        log.info("JOIN roomId={}, user={}", roomId, user);

        int count = registry.join(roomId, user);
        template.convertAndSend("/topic/room/" + roomId,
                new RoomEvent("join", user, roomId, count));
    }

    @MessageMapping("/room/leave")
    public void leave(JoinPayload p) {
        String roomId = StringUtils.hasText(p.roomId()) ? p.roomId() : "ROOM1";
        String user   = StringUtils.hasText(p.user())   ? p.user()   : "guest";
        int count = registry.leave(roomId, user);
        template.convertAndSend("/topic/room/" + roomId,
                new RoomEvent("leave", user, roomId, count));
    }

    @MessageMapping("/room/start")
    public void start(StartPayload p) {
        String roomId = StringUtils.hasText(p.roomId()) ? p.roomId() : "ROOM1";
        log.info("START roomId={}", roomId);
        int count = registry.count(roomId);
        template.convertAndSend("/topic/room/" + roomId,
                new RoomEvent("start", null, roomId, count));
    }
}
