package com.kosta.saladMan.service.empolyee;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.saladMan.controller.common.S3Uploader;
import com.kosta.saladMan.dto.store.EmployeeDto;
import com.kosta.saladMan.entity.store.Employee;
import com.kosta.saladMan.entity.store.Store;
import com.kosta.saladMan.repository.StoreRepository;
import com.kosta.saladMan.repository.empolyee.EmployeeDslRepository;
import com.kosta.saladMan.repository.empolyee.EmployeeRepository;
import com.kosta.saladMan.util.PageInfo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeDslRepository employeeDslRepository;
    private final EmployeeRepository employeeRepository;
    private final StoreRepository storeRepository;
    private final S3Uploader s3Uploader;


    @Override
    public List<EmployeeDto> searchEmployees(String keyword, String grade, Integer storeId, PageInfo pageInfo) {
        long totalCount = employeeDslRepository.countByFilters(keyword, grade, storeId);
        int PAGE_SIZE = 10;
        pageInfo.setAllPage((int) Math.ceil((double)totalCount/PAGE_SIZE));
        int start = (pageInfo.getCurPage()-1)/10*10+1;
        int end = Math.min(start+9, pageInfo.getAllPage());
        pageInfo.setStartPage(start); pageInfo.setEndPage(end);

        List<Employee> list = employeeDslRepository.findByFilters(
            keyword, grade, storeId,
            (pageInfo.getCurPage()-1)*PAGE_SIZE, PAGE_SIZE
        );
        return list.stream().map(emp -> {
            EmployeeDto dto = EmployeeDto.builder()
                    .id(emp.getId())
                    .storeId(emp.getStore().getId())
                    .storeName(emp.getStore().getName())
                    .name(emp.getName())
                    .grade(emp.getGrade())
                    .phone(emp.getPhone())
                    .hireDate(emp.getHireDate())
                    .empStatus(emp.getEmpStatus())
                    .email(emp.getEmail())
                    .address(emp.getAddress())
                    .gender(emp.getGender())
                    .birthday(emp.getBirthday())
                    .imgUrl(emp.getImg())
                    .build();
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EmployeeDto addEmployee(EmployeeDto dto, MultipartFile img) {
        Store store = storeRepository.findById(dto.getStoreId())
            .orElseThrow(() -> new IllegalArgumentException("매장 없음"));
        Employee emp = dto.toEntity();
        emp.setStore(store);
        // 이미지 S3 업로드
        if (img != null && !img.isEmpty()) {
            String imgUrl = null;
			try {
				imgUrl = s3Uploader.upload(img, "employee-img");
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println(imgUrl);
            emp.setImg(imgUrl);
        }
        Employee saved = employeeRepository.save(emp);
        return saved.toDto();
    }
    
    @Override
    @Transactional
    public EmployeeDto updateEmployee(EmployeeDto dto, MultipartFile img) {
        Employee emp = employeeRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("직원 없음"));
        
        emp.setName(dto.getName());
        emp.setGrade(dto.getGrade());
        emp.setPhone(dto.getPhone());
        emp.setHireDate(dto.getHireDate());
        emp.setAddress(dto.getAddress());
        emp.setGender(dto.getGender());
        emp.setBirthday(dto.getBirthday());
        emp.setEmpStatus(dto.getEmpStatus());
        
        if (dto.getStoreId() != null) {
            Store store = storeRepository.findById(dto.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("매장 없음"));
            emp.setStore(store);
        }
        
        // 이미지 교체
        if (img != null && !img.isEmpty()) {
            if (emp.getImg() != null && !emp.getImg().isBlank()) {
                String key = s3Uploader.extractKeyFromUrl(emp.getImg());
                s3Uploader.delete(key);
            }
            
            String imgUrl = null;
			try {
				imgUrl = s3Uploader.upload(img, "employee-img");
			} catch (Exception e) {
				e.printStackTrace();
			}
            emp.setImg(imgUrl);
        }
        Employee saved = employeeRepository.save(emp);
        return saved.toDto();
    }
}
