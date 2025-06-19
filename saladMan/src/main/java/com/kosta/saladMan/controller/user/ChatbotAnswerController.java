package com.kosta.saladMan.controller.user;

import com.kosta.saladMan.dto.chatbot.ChatbotAnswerDto;
import com.kosta.saladMan.service.chatbot.ChatbotAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/chatbot")
@RequiredArgsConstructor
public class ChatbotAnswerController {

    private final ChatbotAnswerService chatbotAnswerService;

    @GetMapping("/answer-by-value")
    public String getAnswerByValueKey(@RequestParam("valueKey") String valueKey) {
        return chatbotAnswerService.getAnswerByValueKey(valueKey);
    }
}
