package com.kosta.saladMan.service.store;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.kosta.saladMan.dto.store.ScheduleDto;
import com.kosta.saladMan.entity.store.Employee;
import com.kosta.saladMan.entity.store.Schedule;
import com.kosta.saladMan.repository.empolyee.EmployeeRepository;
import com.kosta.saladMan.repository.storeManagement.ScheduleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public void saveSchedules(List<ScheduleDto> dtos) {
        if (dtos == null || dtos.isEmpty()) return;
        Integer storeId = null;
        LocalDate min = LocalDate.MAX, max = LocalDate.MIN;
        for (ScheduleDto dto : dtos) {
            if (storeId == null) {
                Employee emp = employeeRepository.findById(dto.getEmployeeId()).orElseThrow();
                storeId = emp.getStore().getId();
            }
            if (dto.getWorkDate().isBefore(min)) min = dto.getWorkDate();
            if (dto.getWorkDate().isAfter(max)) max = dto.getWorkDate();
        }
        scheduleRepository.deleteByEmployee_Store_IdAndWorkDateBetween(storeId, min, max);
        List<Schedule> list = dtos.stream().map(ScheduleDto::toEntity).collect(Collectors.toList());
        scheduleRepository.saveAll(list);
    }

    @Override
    public List<ScheduleDto> getSchedulesByStoreAndMonth(Integer storeId, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        List<Schedule> list = scheduleRepository.findByEmployee_Store_IdAndWorkDateBetween(storeId, start, end);
        return list.stream().map(s -> ScheduleDto.builder()
                .id(s.getId())
                .employeeId(s.getEmployee().getId())
                .workDate(s.getWorkDate())
                .shiftType(s.getShiftType())
                .build()
        ).collect(Collectors.toList());
    }
}
