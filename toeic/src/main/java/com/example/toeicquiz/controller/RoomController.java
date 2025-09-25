//STOMP 메시지 컨트롤러
package com.example.toeicquiz.controller;

import com.example.toeicquiz.dto.GameStart;
import com.example.toeicquiz.dto.JoinPayload;
import com.example.toeicquiz.dto.Presence;
import com.example.toeicquiz.service.RoomRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class RoomController {

    private final SimpMessagingTemplate template;
    private final RoomRegistry roomRegistry;

    // 클라 -> /app/room/join
    @MessageMapping("/room/join")
    public void join(JoinPayload payload) {
        String roomId = payload.getRoomId();
        String user   = payload.getUser();
        int count = roomRegistry.join(roomId, user);

        Presence msg = new Presence("join", user, count);
        template.convertAndSend("/topic/room/" + roomId, msg);
    }

    // 클라 -> /app/room/leave
    @MessageMapping("/room/leave")
    public void leave(JoinPayload payload) {
        String roomId = payload.getRoomId();
        String user   = payload.getUser();
        int count = roomRegistry.leave(roomId, user);

        Presence msg = new Presence("leave", user, count);
        template.convertAndSend("/topic/room/" + roomId, msg);
    }

    // 클라 -> /app/room/start
    @MessageMapping("/room/start")
    public void start(GameStart payload) {
        String roomId = payload.getRoomId();
        int count = roomRegistry.count(roomId);

        Presence msg = new Presence("start", "system", count);
        template.convertAndSend("/topic/room/" + roomId, msg);
        // TODO: 실제 게임 시작 패킷(문제 리스트/타이머 등)도 여기에 함께 push 가능
    }
}


/*
클라이언트는 /app/**로 전송, 서버는 /topic//**로 브로드캐스트
 RoomRegistry로 현재 인원 수를 함께 보내면 프론트에서 UI 갱신이 쉬움
*/
