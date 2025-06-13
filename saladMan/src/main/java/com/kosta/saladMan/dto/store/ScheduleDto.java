package com.kosta.saladMan.dto.store;

import java.time.LocalDate;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.kosta.saladMan.entity.store.Employee;
import com.kosta.saladMan.entity.store.Schedule;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleDto {
    private Integer id;
    private Integer employeeId;
    private LocalDate workDate;
    private String shiftType; 

    public Schedule toEntity() {
        return Schedule.builder()
                .id(id)
                .employee(Employee.builder().id(employeeId).build())
                .workDate(workDate)
                .shiftType(shiftType)
                .build();
    }
}
