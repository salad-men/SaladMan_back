package com.kosta.saladMan.dto.chat;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyChatListResDto {

    private Integer roomId;
    private String roomName;
    private String isGroupChat;
    private Integer unReadCount;
    private Integer storeId;

}
