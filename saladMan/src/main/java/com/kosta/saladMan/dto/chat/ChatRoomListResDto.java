package com.kosta.saladMan.dto.chat;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomListResDto {
    private Integer roomId;
    private String roomName;
}
