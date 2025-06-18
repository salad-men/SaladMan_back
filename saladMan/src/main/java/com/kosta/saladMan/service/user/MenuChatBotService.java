package com.kosta.saladMan.service.user;

import java.util.List;

import com.kosta.saladMan.dto.menu.MenuChatBotDto;

public interface MenuChatBotService {
    List<MenuChatBotDto> getMenusByKeyword(String keyword);
}
