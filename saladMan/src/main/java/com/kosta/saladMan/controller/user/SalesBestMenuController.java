package com.kosta.saladMan.controller.user;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.saladMan.dto.menu.BestMenuDto;
import com.kosta.saladMan.service.user.SalesBestMenuService;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/user/bestmenu")
public class SalesBestMenuController {
    private final SalesBestMenuService bestMenuService;

    @GetMapping("/bestMenus")
    public ResponseEntity<List<BestMenuDto>> getBestMenus() {
        List<BestMenuDto> menus = bestMenuService.getBestMenus();
        return ResponseEntity.ok(menus);
    }
}
