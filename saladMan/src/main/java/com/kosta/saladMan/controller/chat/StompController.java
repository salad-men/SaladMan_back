package com.kosta.saladMan.controller.chat;


import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import com.kosta.saladMan.dto.chat.ChatMessageDto;
import com.kosta.saladMan.entity.chat.ChatMessage;
import com.kosta.saladMan.service.chat.ChatService;

@Controller
public class StompController {

    private final SimpMessageSendingOperations messageTemplate;
    private final ChatService chatService;

    public StompController(SimpMessageSendingOperations messageTemplate, ChatService chatService) {
        this.messageTemplate = messageTemplate;
        this.chatService = chatService;
    }

    //클라이언트에서 특정 publish/roomId형태로 메세지 발행시 MessageMapping이 수신
    @MessageMapping("/{roomId}")
    public void sendMessage(@DestinationVariable Integer roomId, ChatMessageDto chatMessageDto) {
        System.out.println("[STOMP] 수신 roomId=" + roomId + " msg=" + chatMessageDto.getMessage());

        // 메시지 저장(db) + 실제 저장된 엔터티 반환
        ChatMessage saved = chatService.saveMessage(roomId, chatMessageDto);

        // 엔티티를 toDto로 변환해서(= 모든 정보가 채워짐) 브로드캐스트
        ChatMessageDto responseDto = saved.toDto();

        messageTemplate.convertAndSend("/topic/" + roomId, responseDto);
    }

}
