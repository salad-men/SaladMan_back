package com.kosta.saladMan.dto.store;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kosta.saladMan.entity.store.Employee;
import com.kosta.saladMan.entity.store.Store;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDto {
    private Integer id;
    private Integer storeId;
    private String storeName; 
    private String name;
    private String grade;
    private String address;
    private String phone;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String imgUrl;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    private String gender;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate hireDate;
    private String empStatus;
    private String email;


    public Employee toEntity() {
        return Employee.builder()
                .id(id)
                .store(Store.builder().id(storeId).build())
                .name(name)
                .grade(grade)
                .address(address)
                .phone(phone)
                .birthday(birthday)
                .gender(gender)
                .hireDate(hireDate)
                .empStatus(empStatus)
                .email(email)
                .build();
    }
}
