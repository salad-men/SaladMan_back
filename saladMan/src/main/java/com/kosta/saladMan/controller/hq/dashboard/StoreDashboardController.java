package com.kosta.saladMan.controller.hq.dashboard;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.saladMan.dto.dashboard.StoreDashboardSummaryDto;
import com.kosta.saladMan.dto.store.ScheduleDto;
import com.kosta.saladMan.entity.store.Employee;
import com.kosta.saladMan.repository.empolyee.EmployeeRepository;
import com.kosta.saladMan.service.dashboard.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/store/dashboard")
@RequiredArgsConstructor
public class StoreDashboardController {

    private final DashboardService dashboardService;
    private final EmployeeRepository employeeRepository;

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
    public ResponseEntity<?> getWeekSchedule(
        @RequestParam Integer storeId,
        @RequestParam Integer weekNo
    ) {
        // 1. 이번 주 해당 매장의 전체 직원 리스트 조회(이름순)
        List<Employee> employees = employeeRepository.findByStoreIdOrderByNameAsc(storeId);

        // 2. 이번 주의 스케줄 리스트 조회
        List<ScheduleDto> scheduleList = dashboardService.getWeekSchedule(storeId, weekNo);

        // 3. weekStart~weekEnd 날짜 계산
        int year = LocalDate.now().getYear();
        WeekFields weekFields = WeekFields.of(Locale.KOREA);
        LocalDate weekStart = LocalDate.of(year, 1, 4)
                .with(weekFields.weekOfYear(), weekNo)
                .with(weekFields.dayOfWeek(), 1); // 월요일
        // 월~일 7일 리스트
        List<LocalDate> weekDates = new java.util.ArrayList<>();
        for (int i = 0; i < 7; i++) {
            weekDates.add(weekStart.plusDays(i));
        }

        // 4. 2차원 테이블 [직원][요일] => shiftType
        List<List<String>> table = new java.util.ArrayList<>();
        for (Employee emp : employees) {
            List<String> row = new java.util.ArrayList<>();
            for (LocalDate day : weekDates) {
                ScheduleDto sch = scheduleList.stream()
                        .filter(s -> s.getEmployeeId().equals(emp.getId()) && s.getWorkDate().equals(day))
                        .findFirst().orElse(null);
                row.add(sch != null ? sch.getShiftType() : "");
            }
            table.add(row);
        }

        // 5. 직원 이름 리스트
        List<String> empNames = employees.stream().map(Employee::getName).collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
            "empNames", empNames,
            "table", table
        ));
    }
}
