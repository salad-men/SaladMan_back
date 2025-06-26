package com.kosta.saladMan.entity.chat;

import lombok.*;
import javax.persistence.*;

import com.kosta.saladMan.dto.chat.ChatRoomListResDto;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    // 그룹채팅 여부 (Y/N)
    @Builder.Default
    private String isGroupChat = "N";

    // 양방향 관계
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ChatParticipant> chatParticipants = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ChatMessage> chatMessages = new ArrayList<>();
    
    public ChatRoomListResDto toDto() {
        return ChatRoomListResDto.builder()
                .roomId(id)
                .roomName(name)
                .build();
    }

}
