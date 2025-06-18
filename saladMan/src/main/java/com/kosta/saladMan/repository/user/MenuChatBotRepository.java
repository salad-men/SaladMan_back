package com.kosta.saladMan.repository.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.saladMan.entity.menu.TotalMenu;

public interface MenuChatBotRepository extends JpaRepository<TotalMenu, Integer> {
    List<TotalMenu> findByNameContainingIgnoreCase(String keyword);
}
