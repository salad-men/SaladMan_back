package com.kosta.saladMan.entity.chat;

import com.kosta.saladMan.dto.chat.ChatParticipantDto;
import com.kosta.saladMan.entity.store.Store;
import lombok.*;
import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatParticipant extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 참가한 채팅방
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    // 참가자 (store)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;
    
    public ChatParticipantDto toDto() {
        return ChatParticipantDto.builder()
                .id(id)
                .chatRoomId(chatRoom.getId())
                .storeId(store.getId())
                .build();
    }

}
