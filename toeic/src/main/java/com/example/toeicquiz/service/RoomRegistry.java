// "방 상태" 메모리 저장소
package com.example.toeicquiz.service;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RoomRegistry {
    // roomId -> 현재 참가자 집합
    private final Map<String, Set<String>> members = new ConcurrentHashMap<>();

    public synchronized int join(String roomId, String user) {
        members.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(user);
        return members.get(roomId).size();
    }

    public synchronized int leave(String roomId, String user) {
        Set<String> set = members.get(roomId);
        if (set != null) {
            set.remove(user);
            if (set.isEmpty()) members.remove(roomId);
            else return set.size();
        }
        return 0;
    }

    public synchronized int count(String roomId) {
        return members.getOrDefault(roomId, Collections.emptySet()).size();
    }

    public synchronized Set<String> list(String roomId) {
        return new HashSet<>(members.getOrDefault(roomId, Collections.emptySet()));
    }
}
