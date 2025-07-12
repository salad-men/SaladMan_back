package com.kosta.saladMan.service.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import com.kosta.saladMan.dto.notice.AnnounceDto;

public interface AnnounceService {
    Page<AnnounceDto> getAnnounceByType(String type, Pageable pageable);
    AnnounceDto getAnnounceDetail(Integer id); 
    AnnounceDto saveAnnounce(AnnounceDto dto);
    void deleteAnnounce(Integer id);                     //  삭제 
    AnnounceDto updateAnnounce(Integer id, AnnounceDto dto); //  수정 
}