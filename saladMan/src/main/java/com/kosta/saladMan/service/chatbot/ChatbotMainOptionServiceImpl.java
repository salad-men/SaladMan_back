package com.kosta.saladMan.service.chatbot;

import com.kosta.saladMan.dto.chatbot.ChatbotMainOptionDto;
import com.kosta.saladMan.entity.chatbot.ChatbotMainOption;
import com.kosta.saladMan.repository.chatbot.ChatbotMainOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatbotMainOptionServiceImpl implements ChatbotMainOptionService {

    private final ChatbotMainOptionRepository chatbotMainOptionRepository;

    @Override
    public List<ChatbotMainOptionDto> getAllMainOptions() {
        return chatbotMainOptionRepository.findAllByOrderByOrderAsc()
                .stream()
                .map(opt -> ChatbotMainOptionDto.builder()
                        .label(opt.getLabel())
                        .valueKey(opt.getValueKey())
                        .build())
                .collect(Collectors.toList());
    }
}
