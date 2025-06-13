package com.kosta.saladMan.dto.store;

import java.time.LocalDate;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.kosta.saladMan.entity.store.Employee;
import com.kosta.saladMan.entity.store.Store;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDto {
    private Integer id;
    private Integer storeId;
    private String name;
    private String grade;
    private String address;
    private String phone;
    private String img;
    private LocalDate birthday;
    private String gender;
    private LocalDate hireDate;
    private String empStatus;

    public Employee toEntity() {
        return Employee.builder()
                .id(id)
                .store(Store.builder().id(storeId).build())
                .name(name)
                .grade(grade)
                .address(address)
                .phone(phone)
                .img(img)
                .birthday(birthday)
                .gender(gender)
                .hireDate(hireDate)
                .empStatus(empStatus)
                .build();
    }
}
