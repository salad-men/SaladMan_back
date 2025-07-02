package com.kosta.saladMan.dto.chat;


import com.kosta.saladMan.entity.chat.ChatMessage;
import com.kosta.saladMan.entity.chat.ChatRoom;
import com.kosta.saladMan.entity.store.Store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDto {
    private String message;
    private String senderUsername;
    private Integer roomId; 
    private String roomName;
    
    public ChatMessage toEntity(ChatRoom chatRoom, Store store) {
        return ChatMessage.builder()
                .chatRoom(chatRoom)
                .store(store)
                .content(message)
                .build();
    }
}
