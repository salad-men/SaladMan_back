package com.kosta.saladMan.dto.chat;

import com.kosta.saladMan.entity.chat.ChatParticipant;
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
public class ChatParticipantDto {
    private Integer id;
    private Integer chatRoomId;
    private Integer storeId;

    public ChatParticipant toEntity(ChatRoom chatRoom, Store store) {
        return ChatParticipant.builder()
                .chatRoom(chatRoom)
                .store(store)
                .build();
    }
}
