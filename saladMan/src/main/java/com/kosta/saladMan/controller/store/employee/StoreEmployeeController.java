package com.kosta.saladMan.controller.store.employee;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.saladMan.dto.store.EmployeeDto;
import com.kosta.saladMan.service.empolyee.EmployeeService;
import com.kosta.saladMan.util.PageInfo;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/store/emp")
@RequiredArgsConstructor
public class StoreEmployeeController {

    private final EmployeeService employeeService;

    // 자기 매장 직원 목록/검색/페이징 
    @PostMapping("/list")
    public ResponseEntity<Map<String, Object>> list(@RequestBody Map<String, Object> param) {
        String keyword = (String) param.getOrDefault("keyword", "");
        String grade = (String) param.get("grade");
        Integer storeId = param.get("storeId") == null ? null :
                (param.get("storeId") instanceof Number
                        ? ((Number) param.get("storeId")).intValue()
                        : Integer.valueOf(param.get("storeId").toString()));
        int page = param.get("page") == null ? 1 : (int) param.get("page");
        PageInfo pageInfo = new PageInfo(page);
        
        if (storeId == null) {
            return ResponseEntity.badRequest().body(Map.of("msg", "storeId is required"));
        }
        System.out.println(storeId);

        List<EmployeeDto> list = employeeService.searchEmployees(keyword, grade, storeId, pageInfo);
        Map<String, Object> res = new HashMap<>();
        res.put("employees", list);
        res.put("pageInfo", pageInfo);
        return ResponseEntity.ok(res);
    }

    // 직원 수정
    @PostMapping("/update")
    public ResponseEntity<EmployeeDto> update(
            @ModelAttribute EmployeeDto dto,
            @RequestParam(value = "img", required = false) MultipartFile img
    ) {
        EmployeeDto saved = employeeService.updateEmployee(dto, img);
        return ResponseEntity.ok(saved);
    }
}
