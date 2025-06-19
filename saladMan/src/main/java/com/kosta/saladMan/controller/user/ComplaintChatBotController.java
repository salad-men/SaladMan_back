package com.kosta.saladMan.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.kosta.saladMan.dto.notice.ComplaintDto;
import com.kosta.saladMan.service.notice.ComplaintService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user/chatbot/complaints")
@RequiredArgsConstructor
public class ComplaintChatBotController {

    private final ComplaintService complaintService;

    @PostMapping
    public ResponseEntity<ComplaintDto> save(@RequestBody ComplaintDto dto) {
        return ResponseEntity.ok(complaintService.save(dto));
    }
}