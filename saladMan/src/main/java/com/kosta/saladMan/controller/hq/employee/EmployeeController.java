package com.kosta.saladMan.controller.hq.employee;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.saladMan.dto.store.EmployeeDto;
import com.kosta.saladMan.dto.store.StoreDto;
import com.kosta.saladMan.service.empolyee.EmployeeService;
import com.kosta.saladMan.service.inventory.InventoryService;
import com.kosta.saladMan.util.PageInfo;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/hq/emp")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final InventoryService inventoryService;

    // 직원 목록 (페이징+필터)
    @PostMapping("/list")
    public ResponseEntity<Map<String, Object>> list(@RequestBody Map<String, Object> param) {
        String keyword = (String) param.getOrDefault("keyword", "");
        String grade = (String) param.get("grade");
        Integer storeId = param.get("storeId") == null ? null :
                (param.get("storeId") instanceof Number ?
                 ((Number) param.get("storeId")).intValue() :
                 Integer.valueOf(param.get("storeId").toString()));
        int page = param.get("page") == null ? 1 : (int) param.get("page");
        PageInfo pageInfo = new PageInfo(page);

        List<EmployeeDto> list = employeeService.searchEmployees(keyword, grade, storeId, pageInfo);
        Map<String, Object> res = new HashMap<>();
        res.put("employees", list);
        res.put("pageInfo", pageInfo);
        return ResponseEntity.ok(res);
    }

    // 매장 목록
    @GetMapping("/stores")
    public ResponseEntity<Map<String, Object>> stores() {
        List<StoreDto> stores = inventoryService.getAllStores();
        return ResponseEntity.ok(Map.of("stores", stores));
    }

    // 직원 등록 (파일 업로드, ModelAttribute)
    @PostMapping("/add")
    public ResponseEntity<EmployeeDto> add(
            @ModelAttribute EmployeeDto dto,
            @RequestParam(value = "img", required = false) MultipartFile img
    ) {
        System.out.println("생년월일: " + dto.getBirthday());
        System.out.println("성별: " + dto.getGender());
        System.out.println("주소: " + dto.getAddress());
        System.out.println("입사일: " + dto.getHireDate());
    	System.out.println(img != null ? img.getOriginalFilename() : "no img");

        EmployeeDto saved = employeeService.addEmployee(dto, img);
        return ResponseEntity.ok(saved);
    }

    // 직원 수정 (파일 업로드, ModelAttribute)
    @PostMapping("/update")
    public ResponseEntity<EmployeeDto> update(
            @ModelAttribute EmployeeDto dto,
            @RequestParam(value = "img", required = false) MultipartFile img
    ) {
        EmployeeDto saved = employeeService.updateEmployee(dto, img);
        return ResponseEntity.ok(saved);
    }
}
