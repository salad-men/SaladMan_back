package com.kosta.saladMan.controller.user;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.saladMan.entity.userBanner.UserBanner;
import com.kosta.saladMan.service.user.UserBannerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user/banner")
@RequiredArgsConstructor
public class UserBannerController {

    private final UserBannerService userBannerService;

    @GetMapping("/{id}")
    public UserBanner getBanner(@PathVariable Long id) {
        return userBannerService.getBanner(id);
    }

    @PreAuthorize("hasRole('HQ')")
    @PatchMapping("/{id}")
    public UserBanner updateBanner(@PathVariable Long id, @RequestBody UserBanner dto) {
        return userBannerService.updateBanner(id, dto);
    }
}