package com.kosta.saladMan.controller.user;

import com.kosta.saladMan.dto.chatbot.ChatbotQuestionDto;
import com.kosta.saladMan.service.chatbot.ChatbotQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/chatbot")
@RequiredArgsConstructor
public class ChatbotQuestionController {

    private final ChatbotQuestionService chatbotQuestionService;

    @GetMapping("/question")
    public List<ChatbotQuestionDto> getQuestions(@RequestParam("mainOptionId") int mainOptionId) {
        return chatbotQuestionService.getQuestionsByMainOptionId(mainOptionId);
    }
}
