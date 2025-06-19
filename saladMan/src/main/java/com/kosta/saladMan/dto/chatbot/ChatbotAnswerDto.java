package com.kosta.saladMan.dto.chatbot;

import com.kosta.saladMan.entity.chatbot.ChatbotAnswer;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatbotAnswerDto {

    private Integer id;
    private String answerText;
    private boolean dynamic;
    private String valueKey;

    public static ChatbotAnswerDto fromEntity(ChatbotAnswer answer) {
        return ChatbotAnswerDto.builder()
                .id(answer.getId())
                .answerText(answer.getAnswerText())
                .dynamic(answer.isDynamic())
                .valueKey(answer.getValueKey())
                .build();
    }
}
