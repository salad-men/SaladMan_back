package com.kosta.saladMan.dto.chatbot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ChatbotComplaintDto {
    private Integer storeId;
    private String title;
    private String content;
    private String writerEmail;
    private String writerNickname;
    private String writerDate;
}
