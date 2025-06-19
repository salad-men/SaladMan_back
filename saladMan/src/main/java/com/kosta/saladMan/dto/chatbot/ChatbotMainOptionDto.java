package com.kosta.saladMan.dto.chatbot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatbotMainOptionDto {
    private String label;     // 프론트에 보여줄 버튼 이름 메뉴, 자주묻는 질문 등등
    private String valueKey;  // 내부에서 사용하는 값 "menu", "store" 등등
}
