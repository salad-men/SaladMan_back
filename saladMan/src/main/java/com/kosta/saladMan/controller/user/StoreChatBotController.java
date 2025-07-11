package com.kosta.saladMan.controller.user;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.saladMan.dto.store.StoreDto;
import com.kosta.saladMan.entity.store.Store;
import com.kosta.saladMan.service.user.StoreChatBotService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class StoreChatBotController {

    private final StoreChatBotService storeChatBotService;

    @GetMapping("/user/chatbot/stores")
    public List<StoreDto> getStoresByKeyword(@RequestParam String keyword) {
        return storeChatBotService.searchStores(keyword);
    }
    
}
