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
        @RequestParam(required = false, defaultValue = "day") String groupType,
        @RequestParam(required = false, defaultValue = "0") int weekNo
    ) {
        return dashboardService.getStoreSummary(storeId, startDate, endDate, groupType, weekNo);
    }

    @GetMapping("/week-schedule")
    public ResponseEntity<List<ScheduleDto>> getWeekSchedule(
        @RequestParam Integer storeId,
        @RequestParam Integer weekNo
    ) {
        List<ScheduleDto> schedule = dashboardService.getWeekSchedule(storeId, weekNo);
        return ResponseEntity.ok(schedule);
    }
}
