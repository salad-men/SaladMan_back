package com.kosta.saladMan.config.chat;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.kosta.saladMan.config.jwt.JwtProperties;
import com.kosta.saladMan.service.chat.ChatService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;

@Component
public class StompHandler implements ChannelInterceptor {

    private final ChatService chatService;

    public StompHandler(ChatService chatService) {
        this.chatService = chatService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String bearerToken = accessor.getFirstNativeHeader(JwtProperties.HEADER_STRING);

        // CONNECT, SUBSCRIBE일 때만 토큰 검사!
        if (StompCommand.CONNECT == accessor.getCommand() || StompCommand.SUBSCRIBE == accessor.getCommand()) {
            if (bearerToken == null || !bearerToken.startsWith(JwtProperties.TOKEN_PREFIX)) {
                throw new AuthenticationServiceException("토큰이 없거나 형식이 올바르지 않습니다.");
            }
            String token = bearerToken.substring(JwtProperties.TOKEN_PREFIX.length());
            String username;
            try {
                username = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET))
                        .build()
                        .verify(token)
                        .getSubject();
                System.out.println("[WebSocket 인증] user: " + username);
            } catch (Exception e) {
                throw new AuthenticationServiceException("토큰이 유효하지 않습니다.");
            }

            // SUBSCRIBE일 경우만 채팅방 참여권한 확인
            if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
                String dest = accessor.getDestination();
                if (dest == null || !dest.startsWith("/topic/")) {
                    throw new AuthenticationServiceException("구독 경로가 잘못되었습니다.");
                }
                String roomIdStr = dest.substring("/topic/".length());
                Integer roomId;
                try {
                    roomId = Integer.parseInt(roomIdStr);
                } catch (NumberFormatException e) {
                    throw new AuthenticationServiceException("방 번호 형식이 잘못되었습니다.");
                }
                // 참여자 권한 체크
                if (!chatService.isRoomParticipant(username, roomId)) {
                    throw new AuthenticationServiceException("해당 room에 권한이 없습니다.");
                }
            }
        }
        // SEND (메시지 전송)는 인증 안함! (WebSocket 세션 기반 처리)
        return message;
    }
}
