package com.kosta.saladMan.controller.user;

import com.kosta.saladMan.service.chatbot.ChatbotAnswerService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/chatbot")
public class ChatbotIngredientSearchController {

    private final ChatbotAnswerService ChatbotAnswerService;

    @GetMapping("/ingredient")
    public List<Map<String, String>> getMenusByIngredient(@RequestParam String keyword) {
        return ChatbotAnswerService.findMenusByIngredientName(keyword);
    }
}
