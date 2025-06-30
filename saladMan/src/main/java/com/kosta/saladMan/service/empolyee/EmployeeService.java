package com.kosta.saladMan.service.empolyee;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.kosta.saladMan.dto.store.EmployeeDto;
import com.kosta.saladMan.util.PageInfo;

public interface EmployeeService {
    List<EmployeeDto> searchEmployees(String keyword, String grade, Integer storeId, PageInfo pageInfo);
    EmployeeDto addEmployee(EmployeeDto dto, MultipartFile img);
    EmployeeDto updateEmployee(EmployeeDto dto, MultipartFile img);

}
