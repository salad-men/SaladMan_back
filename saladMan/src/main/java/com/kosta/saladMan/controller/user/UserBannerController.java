package com.kosta.saladMan.controller.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.saladMan.controller.common.S3Uploader;
import com.kosta.saladMan.entity.userBanner.UserBanner;
import com.kosta.saladMan.service.user.UserBannerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user/banner")
@RequiredArgsConstructor
public class UserBannerController {

    private final UserBannerService userBannerService;
    private final S3Uploader s3Uploader;

    @GetMapping("/{id}")
    public UserBanner getBanner(@PathVariable Long id) {
        return userBannerService.getBanner(id);
    }

    @PatchMapping("/{id}")
    public UserBanner updateBanner(@PathVariable Long id, @RequestBody UserBanner dto) {
        return userBannerService.updateBanner(id, dto);
    }

    @PostMapping("/upload")
    public Map<String, String> upload(@RequestParam("file") MultipartFile file) throws Exception {
        String url = s3Uploader.upload(file, "userBanner");
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        return result;
    }
}
