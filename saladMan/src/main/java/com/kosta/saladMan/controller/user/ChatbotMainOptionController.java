package com.kosta.saladMan.controller.user;

import com.kosta.saladMan.dto.chatbot.ChatbotMainOptionDto;
import com.kosta.saladMan.service.chatbot.ChatbotMainOptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/chatbot")
@RequiredArgsConstructor
public class ChatbotMainOptionController {

    private final ChatbotMainOptionService chatbotMainOptionService;

    @GetMapping("/main-options")
    public List<ChatbotMainOptionDto> getMainOptions() {
        return chatbotMainOptionService.getAllMainOptions();
    }
}
