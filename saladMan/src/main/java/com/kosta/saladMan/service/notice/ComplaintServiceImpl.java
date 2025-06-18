package com.kosta.saladMan.service.notice;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.saladMan.dto.notice.ComplaintDto;
import com.kosta.saladMan.repository.notice.ComplaintRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ComplaintServiceImpl implements ComplaintService {
    private final ComplaintRepository complaintRepository;

    @Override
    public ComplaintDto save(ComplaintDto dto) {
        return complaintRepository.save(dto.toEntity()).toDto();
    }
}
