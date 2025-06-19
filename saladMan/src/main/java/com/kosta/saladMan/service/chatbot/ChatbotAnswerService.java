package com.kosta.saladMan.service.chatbot;

import com.kosta.saladMan.dto.chatbot.ChatbotAnswerDto;

import java.util.List;

public interface ChatbotAnswerService {
    String getAnswerByValueKey(String valueKey);
}
