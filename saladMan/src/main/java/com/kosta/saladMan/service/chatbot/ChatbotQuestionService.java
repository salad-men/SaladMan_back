package com.kosta.saladMan.service.chatbot;

import com.kosta.saladMan.dto.chatbot.ChatbotQuestionDto;

import java.util.List;

public interface ChatbotQuestionService {
    List<ChatbotQuestionDto> getQuestionsByMainOptionId(int mainOptionId);
}
