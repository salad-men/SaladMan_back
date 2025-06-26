package com.kosta.saladMan.dto.chat;

import com.kosta.saladMan.entity.chat.ChatMessage;
import com.kosta.saladMan.entity.chat.ChatRoom;
import com.kosta.saladMan.entity.chat.ReadStatus;
import com.kosta.saladMan.entity.store.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReadStatusDto {
    private Integer id;
    private Integer chatRoomId;
    private Integer storeId;
    private Integer chatMessageId;
    private Boolean isRead;

    public ReadStatus toEntity(ChatRoom chatRoom, Store store, ChatMessage chatMessage) {
        return ReadStatus.builder()
                .chatRoom(chatRoom)
                .store(store)
                .chatMessage(chatMessage)
                .isRead(isRead)
                .build();
    }
}
