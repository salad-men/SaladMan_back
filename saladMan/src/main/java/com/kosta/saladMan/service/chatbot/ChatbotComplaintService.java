package com.kosta.saladMan.service.chatbot;

import com.kosta.saladMan.dto.notice.ComplaintDto;

public interface ChatbotComplaintService {
    void saveComplaint(ComplaintDto dto);
}
