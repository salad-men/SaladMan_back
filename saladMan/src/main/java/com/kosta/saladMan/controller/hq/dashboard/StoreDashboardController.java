package com.kosta.saladMan.controller.hq.dashboard;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.saladMan.dto.dashboard.StoreDashboardSummaryDto;
import com.kosta.saladMan.dto.store.ScheduleDto;
import com.kosta.saladMan.service.dashboard.DashboardService;
import com.kosta.saladMan.service.inventory.InventoryService;
import com.kosta.saladMan.service.saleOrder.SalesService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/store/dashboard")
@RequiredArgsConstructor
public class StoreDashboardController {

    private final DashboardService dashboardService;


    @GetMapping("/summary")
    public StoreDashboardSummaryDto getStoreSummary(
        @RequestParam Integer storeId,
        @RequestParam String startDate,
        @RequestParam String endDate,
        @RequestParam(required=false, defaultValue="0") int weekNo
    ) {
        return dashboardService.getStoreSummary(storeId, startDate, endDate, weekNo);
    }

    // 주간 근무표 조회 (year 파라미터 제거)
    @GetMapping("/week-schedule")
    public ResponseEntity<List<ScheduleDto>> getWeekSchedule(
        @RequestParam Integer storeId,
        @RequestParam Integer weekNo // year 파라미터를 제거하고 weekNo만 사용
    ) {
        // 주간 근무표를 가져오는 서비스 로직 호출
        List<ScheduleDto> schedule = dashboardService.getWeekSchedule(storeId, weekNo);
        
        // 주간 근무표를 반환
        return ResponseEntity.ok(schedule);
    }
}
