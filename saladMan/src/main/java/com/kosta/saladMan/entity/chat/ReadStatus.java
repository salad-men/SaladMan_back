package com.kosta.saladMan.entity.chat;

import com.kosta.saladMan.dto.chat.ReadStatusDto;
import com.kosta.saladMan.entity.store.Store;
import lombok.*;
import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReadStatus extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 소속 채팅방
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    // 읽은 사람 (store)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    // 어떤 메시지에 대한 읽음상태인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_message_id", nullable = false)
    private ChatMessage chatMessage;

    @Column(nullable = false)
    private Boolean isRead;

    public void updateIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
    
    public ReadStatusDto toDto() {
        return ReadStatusDto.builder()
                .id(id)
                .chatRoomId(chatRoom.getId())
                .storeId(store.getId())
                .chatMessageId(chatMessage.getId())
                .isRead(isRead)
                .build();
    }

}
