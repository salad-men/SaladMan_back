package com.kosta.saladMan.service.chatbot;

import org.springframework.stereotype.Service;

import com.kosta.saladMan.dto.notice.ComplaintDto;
import com.kosta.saladMan.entity.notice.Complaint;
import com.kosta.saladMan.entity.store.Store;
import com.kosta.saladMan.repository.notice.ComplaintRepository;
import com.kosta.saladMan.repository.user.StoreChatBotRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatbotComplaintServiceImpl implements ChatbotComplaintService {

    private final ComplaintRepository complaintRepository;
    private final StoreChatBotRepository storeRepository;

    @Override
    public void saveComplaint(ComplaintDto dto) {
        Store store = storeRepository.findById(dto.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("해당 매장을 찾을 수 없습니다."));

        Complaint complaint = Complaint.builder()
                .store(store)
                .title(dto.getTitle())
                .content(dto.getContent())
                .writerDate(dto.getWriterDate()) // LocalDate 타입
                .writerEmail(dto.getWriterEmail())
                .writerNickname(dto.getWriterNickname())
                .isHqRead(false)
                .isStoreRead(false)
                .isRelay(false)
                .build();

        complaintRepository.save(complaint);
    }
}
