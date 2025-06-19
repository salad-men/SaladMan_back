package com.kosta.saladMan.controller.user;

import com.kosta.saladMan.dto.menu.TotalMenuDto;
import com.kosta.saladMan.service.user.MenuService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    // GET /user/menus?categoryId=1
    @GetMapping
    public List<TotalMenuDto> getMenusByCategory(@RequestParam Integer categoryId) {
        return menuService.getMenusByCategory(categoryId);
    }
}
