package com.kosta.saladMan.controller.user;

import com.kosta.saladMan.dto.notice.ComplaintDto;
import com.kosta.saladMan.service.chatbot.ChatbotComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/chatbot")
@RequiredArgsConstructor
public class ChatbotComplaintController {

    private final ChatbotComplaintService chatbotComplaintService;

    @PostMapping("/complaints")
    public ResponseEntity<String> receiveComplaint(@RequestBody ComplaintDto dto) {
        chatbotComplaintService.saveComplaint(dto);
        return ResponseEntity.ok("접수 완료!");
    }
}
