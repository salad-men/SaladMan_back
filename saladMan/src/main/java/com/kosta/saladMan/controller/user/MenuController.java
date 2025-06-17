package com.kosta.saladMan.controller.user;

import com.kosta.saladMan.dto.menu.TotalMenuDto;
import com.kosta.saladMan.service.user.MenuService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    // GET /api/menus?categoryId=1
    @GetMapping
    public List<TotalMenuDto> getMenusByCategory(@RequestParam Integer categoryId) {
        return menuService.getMenusByCategory(categoryId);
    }
}
