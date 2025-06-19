package com.kosta.saladMan.entity.chatbot;

import lombok.*;

import javax.persistence.*;

import com.kosta.saladMan.dto.chatbot.ChatbotAnswerDto;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "chatbot_answer")
public class ChatbotAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "question_id")
    private Integer questionId;

    @Column(name = "answer_text", length = 1000)
    private String answerText;

    @Column(name = "is_dynamic")
    private boolean dynamic;

    @Column(name = "value_key")
    private String valueKey;
    
    public ChatbotAnswerDto toDto() {
        return ChatbotAnswerDto.builder()
                .id(this.id)
                .answerText(this.answerText)
                .dynamic(this.dynamic)
                .valueKey(this.valueKey)
                .build();
    }
}
