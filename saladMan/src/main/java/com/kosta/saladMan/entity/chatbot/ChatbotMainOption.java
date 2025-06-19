package com.kosta.saladMan.entity.chatbot;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.DynamicInsert;

import com.kosta.saladMan.dto.chatbot.ChatbotMainOptionDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@DynamicInsert
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatbotMainOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String label;     // 프론트에 보여줄 이름

    private String valueKey;  // 내부에서 사용할 키값

    @Column(name = "display_order")
    private int order;        // 정렬 순서
    
    public ChatbotMainOptionDto toDto() {
        return ChatbotMainOptionDto.builder()
                .label(this.label)
                .valueKey(this.valueKey)
                .build();
    }
}
