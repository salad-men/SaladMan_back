package com.kosta.saladMan.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.saladMan.service.chatbot.ChatbotAnswerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user/chatbot")
@RequiredArgsConstructor
public class ChatbotAnswerController {

    private final ChatbotAnswerService chatbotAnswerService;

    @GetMapping("/answer")
    public ResponseEntity<String> getAnswer(@RequestParam("valueKey") String valueKey) {
        String answer = chatbotAnswerService.getAnswerByValueKey(valueKey);
        return ResponseEntity.ok(answer);
    }


}
