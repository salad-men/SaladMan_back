package com.kosta.saladMan.controller.user;

import com.kosta.saladMan.dto.chatbot.StoreTimeResponseDto;
import com.kosta.saladMan.service.chatbot.ChatBotStoreService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/chatbot")
@RequiredArgsConstructor
public class ChatBotStoreController {

    private final ChatBotStoreService chatBotstoreService;
    
    @GetMapping("/store-time")
    public List<StoreTimeResponseDto> getStoreTimes(@RequestParam String keyword) {
        return chatBotstoreService.getStoreTimesByKeyword(keyword);
    }
}
