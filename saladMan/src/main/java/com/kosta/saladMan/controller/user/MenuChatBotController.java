package com.kosta.saladMan.controller.user;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.saladMan.dto.menu.MenuChatBotDto;
import com.kosta.saladMan.service.user.MenuChatBotService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MenuChatBotController {

    private final MenuChatBotService menuChatBotService;

    @GetMapping("/user/chatbot/menus")
    public List<MenuChatBotDto> getMenusByKeyword(@RequestParam String keyword) {
        return menuChatBotService.getMenusByKeyword(keyword);
    }
}
