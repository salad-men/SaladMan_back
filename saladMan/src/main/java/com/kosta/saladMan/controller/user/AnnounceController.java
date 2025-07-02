package com.kosta.saladMan.controller.user;

import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.saladMan.dto.notice.AnnounceDto;
import com.kosta.saladMan.service.user.AnnounceService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;


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

}