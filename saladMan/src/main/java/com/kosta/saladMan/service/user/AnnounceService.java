package com.kosta.saladMan.service.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import com.kosta.saladMan.dto.notice.AnnounceDto;

public interface AnnounceService {
    Page<AnnounceDto> getAnnounceByType(String type, Pageable pageable);
    AnnounceDto getAnnounceDetail(Integer id); 
}