package com.kosta.saladMan.entity.chat;

import com.kosta.saladMan.dto.chat.ChatMessageDto;
import com.kosta.saladMan.entity.store.Store;
import lombok.*;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 소속 채팅방
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    // 작성자 (store)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(nullable = false, length = 500)
    private String content;

    // 읽음 상태 (양방향)
    @OneToMany(mappedBy = "chatMessage", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ReadStatus> readStatuses = new ArrayList<>();
    
    
    public ChatMessageDto toDto() {
        return ChatMessageDto.builder()
                .message(content)
                .senderUsername(store.getUsername())
                .roomId(chatRoom.getId())     
                .roomName(chatRoom.getName()) 
                .build();
    }
}
