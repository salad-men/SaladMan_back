// src/main/java/com/kosta/saladMan/service/chat/ChatSseService.java

package com.kosta.saladMan.service.chat;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatSseService {

    // username 별로 emitter를 관리
    private final Map<String, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String username) {
        SseEmitter emitter = new SseEmitter(60L * 60 * 1000); // 1시간 유지

        emitters.computeIfAbsent(username, key -> new ArrayList<>()).add(emitter);

        emitter.onCompletion(() -> emitters.get(username).remove(emitter));
        emitter.onTimeout(() -> emitters.get(username).remove(emitter));
        emitter.onError(e -> emitters.get(username).remove(emitter));

        return emitter;
    }

    // username에 알림 push
    public void sendToUser(String username, String eventName, Object data) {
    	System.out.println("[SSE] sendToUser: " + username + ", event=" + eventName + ", data=" + data);
        List<SseEmitter> userEmitters = emitters.getOrDefault(username, Collections.emptyList());
        Iterator<SseEmitter> iterator = userEmitters.iterator();
        while (iterator.hasNext()) {
            SseEmitter emitter = iterator.next();
            try {
                emitter.send(SseEmitter.event().name(eventName).data(data));
            } catch (Exception e) {
                iterator.remove();  // **iterator로 삭제**
            }
        }
    }


    // 여러명(채팅방 참여자 전체)에게 알림 push
    public void sendToUsers(Collection<String> usernames, String eventName, Object data) {
        for (String username : usernames) {
            System.out.println("[SSE] " + eventName + " push to: " + username + ", data=" + data);
            sendToUser(username, eventName, data);
        }
    }
}
