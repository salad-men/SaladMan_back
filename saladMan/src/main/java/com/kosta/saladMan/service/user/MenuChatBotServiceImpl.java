package com.kosta.saladMan.service.user;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.kosta.saladMan.dto.menu.MenuChatBotDto;
import com.kosta.saladMan.repository.user.MenuChatBotRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuChatBotServiceImpl implements MenuChatBotService {

    private final MenuChatBotRepository menuChatBotRepository;

    @Override
    public List<MenuChatBotDto> getMenusByKeyword(String keyword) {
        return menuChatBotRepository.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(menu -> new MenuChatBotDto(menu.getName(), menu.getSalePrice()))
                .collect(Collectors.toList());
    }
}
