package com.kosta.saladMan.service.chatbot;

import com.kosta.saladMan.dto.chatbot.ChatbotQuestionDto;
import com.kosta.saladMan.repository.chatbot.ChatbotQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatbotQuestionServiceImpl implements ChatbotQuestionService {

    private final ChatbotQuestionRepository chatbotQuestionRepository;

    @Override
    public List<ChatbotQuestionDto> getQuestionsByMainOptionId(int mainOptionId) {
        return chatbotQuestionRepository.findByMainOptionIdOrderByDisplayOrderAsc(mainOptionId)
                .stream()
                .map(entity -> entity.toDto())
                .collect(Collectors.toList());
    }
}
