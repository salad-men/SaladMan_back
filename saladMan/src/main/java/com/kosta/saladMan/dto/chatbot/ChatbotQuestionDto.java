package com.kosta.saladMan.dto.chatbot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotQuestionDto {
    private int id;
    private int mainOptionId;
    private String question;
    private String valueKey;
    private int displayOrder;
    private int visible;
}
