package com.kosta.saladMan.service.chatbot;

import com.kosta.saladMan.dto.chatbot.ChatbotAnswerDto;

import java.util.List;
import java.util.Map;

public interface ChatbotAnswerService {
    String getAnswerByValueKey(String valueKey);
    List<Map<String, String>> findMenusByIngredientName(String ingredientName);
}
