package com.example.toeicquiz.service;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoomRegistry {

    private final Map<String, Set<String>> members = new ConcurrentHashMap<>();

    public int join(String roomId, String user) {
        Assert.hasText(roomId, "roomId must not be empty");
        if (!StringUtils.hasText(user)) user = "guest";
        Set<String> set = members.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet());
        set.add(user);
        return set.size();
    }

    public int leave(String roomId, String user) {
        if (!StringUtils.hasText(roomId)) return 0;
        Set<String> set = members.get(roomId);
        if (set == null) return 0;
        if (StringUtils.hasText(user)) set.remove(user);
        if (set.isEmpty()) members.remove(roomId);
        return set.size();
    }

    public int count(String roomId) {
        if (!StringUtils.hasText(roomId)) return 0;
        return members.getOrDefault(roomId, Collections.emptySet()).size();
    }
}
