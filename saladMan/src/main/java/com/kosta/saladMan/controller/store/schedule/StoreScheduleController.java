package com.kosta.saladMan.controller.store.schedule;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.saladMan.dto.store.ScheduleDto;
import com.kosta.saladMan.service.store.ScheduleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/store/schedule")
@RequiredArgsConstructor
public class StoreScheduleController {

    private final ScheduleService scheduleService;

    // 월간 스케줄 등록
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody List<ScheduleDto> dtos) {
        scheduleService.saveSchedules(dtos);
        return ResponseEntity.ok(Map.of("result", "ok"));
    }

    // 매장 월간 스케줄 조회
    @GetMapping("/month")
    public ResponseEntity<List<ScheduleDto>> getMonth(
            @RequestParam Integer storeId,
            @RequestParam Integer year,
            @RequestParam Integer month
    ) {
        List<ScheduleDto> list = scheduleService.getSchedulesByStoreAndMonth(storeId, year, month);
        return ResponseEntity.ok(list);
    }
}
