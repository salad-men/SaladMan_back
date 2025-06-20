package com.kosta.saladMan.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.saladMan.service.TestServiceImpl;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {

    private final TestServiceImpl testService;

    // 단일 파일 업로드 후 CloudFront URL 반환
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String url = testService.uploadFile(file);
            return ResponseEntity.ok(url); // 프론트엔드에 URL만 반환
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity.badRequest().body("업로드 실패: " + e.getMessage());
        }
    }
}
