package com.kosta.saladMan.controller.user;

import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.kosta.saladMan.dto.notice.AnnounceDto;
import com.kosta.saladMan.service.user.AnnounceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class AnnounceController {

    private final AnnounceService announceService;

    @GetMapping("/announce")
    public Page<AnnounceDto> getAnnounceByType(
        @RequestParam String type,
        @PageableDefault(size = 10, sort = "postedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return announceService.getAnnounceByType(type, pageable);
    }

    @GetMapping("/announce/{id}")
    public AnnounceDto getAnnounceDetail(@PathVariable Integer id) {
        return announceService.getAnnounceDetail(id);
    }

    @PostMapping("/announce")
    public AnnounceDto createAnnounce(@RequestBody AnnounceDto dto) {
        return announceService.saveAnnounce(dto);
    }
    @DeleteMapping("/announce/{id}")
    public void deleteAnnounce(@PathVariable Integer id) {
        announceService.deleteAnnounce(id);
    }

    @PutMapping("/announce/{id}")
    public AnnounceDto updateAnnounce(
        @PathVariable Integer id,
        @RequestBody AnnounceDto dto
    ) {
        return announceService.updateAnnounce(id, dto);
    }
}
