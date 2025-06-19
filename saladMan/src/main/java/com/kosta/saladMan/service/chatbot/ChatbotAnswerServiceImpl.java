package com.kosta.saladMan.service.chatbot;

import org.springframework.stereotype.Service;

import com.kosta.saladMan.dto.chatbot.ChatbotAnswerDto;
import com.kosta.saladMan.entity.chatbot.ChatbotAnswer;
import com.kosta.saladMan.repository.chatbot.ChatbotAnswerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatbotAnswerServiceImpl implements ChatbotAnswerService {

    private final ChatbotAnswerRepository chatbotAnswerRepository;

    @Override
    public String getAnswerByValueKey(String valueKey) {
        return chatbotAnswerRepository.findByValueKey(valueKey)
            .map(ChatbotAnswer::getAnswerText) // ✅ entity의 getter 사용
            .orElse("등록된 답변이 없습니다.");
    }
}
