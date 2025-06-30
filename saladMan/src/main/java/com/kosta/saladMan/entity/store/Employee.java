package com.kosta.saladMan.entity.store;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Column;
import javax.persistence.FetchType;

import org.hibernate.annotations.DynamicInsert;

import com.kosta.saladMan.dto.store.EmployeeDto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@DynamicInsert
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "store_id")
    private Store store;

    @Column(nullable = false)
    private String name;

    private String grade;

    private String address;

    private String phone;

    private String img;

    private LocalDate birthday;

    private String gender;

    private LocalDate hireDate;

    private String empStatus;
    
    private String email; 

    
    public EmployeeDto toDto() {
        return EmployeeDto.builder()
                .id(this.id)
                .storeId(this.store != null ? this.store.getId() : null)
                .storeName(this.store != null ? this.store.getName() : null) 
                .name(this.name)
                .grade(this.grade)
                .address(this.address)
                .phone(this.phone)
                .img(this.img)
                .birthday(this.birthday)
                .gender(this.gender)
                .hireDate(this.hireDate)
                .empStatus(this.empStatus)
                .email(email)
                .build();
    }
}
