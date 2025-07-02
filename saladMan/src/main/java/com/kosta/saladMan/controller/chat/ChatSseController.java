// src/main/java/com/kosta/saladMan/controller/chat/ChatSseController.java

package com.kosta.saladMan.controller.chat;

import com.kosta.saladMan.service.chat.ChatSseService;
import com.kosta.saladMan.service.chat.ChatService;
import com.kosta.saladMan.entity.chat.ChatMessage;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.kosta.saladMan.config.jwt.JwtProperties;
import com.kosta.saladMan.dto.chat.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatSseController {

    private final ChatSseService chatSseService;
    private final ChatService chatService;

    // 1. SSE 구독 (로그인 유저)
    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeSSE(@RequestParam("token") String token) {
        System.out.println("[SSE] token 파라미터: " + token);
        try {
            String username = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET))
                    .build()
                    .verify(token)
                    .getSubject();
            if(username == null || username.isEmpty()) {
                throw new RuntimeException("토큰이 유효하지 않음");
            }
            return chatSseService.subscribe(username);
        } catch(Exception ex) {
            System.out.println("[SSE] JWT 파싱 실패: " + ex);
            throw new RuntimeException("SSE 토큰 인증 실패!", ex);
        }
    }


    
    // DTO 예시
    public static class ReadMessageDto {
        public Integer roomId;
        public String username;
        public ReadMessageDto(Integer roomId, String username) {
            this.roomId = roomId; this.username = username;
        }
    }
}
