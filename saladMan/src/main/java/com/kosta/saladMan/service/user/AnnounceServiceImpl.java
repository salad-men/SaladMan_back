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
    
    @Override
    public AnnounceDto saveAnnounce(AnnounceDto dto) {
        Announce announce = dto.toEntity();
        announce.setPostedAt(java.time.LocalDate.now());
        announce.setViewCnt(0);

        Announce saved = announceRepository.save(announce);

        return AnnounceDto.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .content(saved.getContent())
                .postedAt(saved.getPostedAt())
                .viewCnt(saved.getViewCnt())
                .img(saved.getImg())
                .type(saved.getType())
                .build();
    }
    
    @Override
    public void deleteAnnounce(Integer id) {
        announceRepository.deleteById(id);
    }

    @Override
    public AnnounceDto updateAnnounce(Integer id, AnnounceDto dto) {
        Announce announce = announceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 공지사항"));

        announce.setTitle(dto.getTitle());
        announce.setContent(dto.getContent());
        announce.setImg(dto.getImg());
        announce.setType(dto.getType());
        // postedAt은 그대로 두고 싶으면 수정 안 함

        Announce updated = announceRepository.save(announce);

        return AnnounceDto.builder()
                .id(updated.getId())
                .title(updated.getTitle())
                .content(updated.getContent())
                .postedAt(updated.getPostedAt())
                .viewCnt(updated.getViewCnt())
                .img(updated.getImg())
                .type(updated.getType())
                .build();
    }

}
