package com.kosta.saladMan.service.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.kosta.saladMan.dto.notice.AnnounceDto;
import com.kosta.saladMan.entity.notice.Announce;
import com.kosta.saladMan.repository.user.AnnounceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnnounceServiceImpl implements AnnounceService {

    private final AnnounceRepository announceRepository;

    @Override
    public Page<AnnounceDto> getAnnounceByType(String type, Pageable pageable) {
        return announceRepository.findByTypeOrderByPostedAtDesc(type, pageable)
                .map(announce -> AnnounceDto.builder()
                        .id(announce.getId())
                        .title(announce.getTitle())
                        .content(announce.getContent())
                        .postedAt(announce.getPostedAt())
                        .viewCnt(announce.getViewCnt())
                        .img(announce.getImg())
                        .type(announce.getType())
                        .build());
    }
    
    @Override
    public AnnounceDto getAnnounceDetail(Integer id) {
        Announce announce = announceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 공지사항"));

        announce.setViewCnt((announce.getViewCnt() == null ? 0 : announce.getViewCnt()) + 1);
        announceRepository.save(announce);

        return AnnounceDto.builder()
                .id(announce.getId())
                .title(announce.getTitle())
                .content(announce.getContent())
                .postedAt(announce.getPostedAt())
                .viewCnt(announce.getViewCnt())
                .img(announce.getImg())
                .type(announce.getType())
                .build();
    }
}
