package com.kosta.saladMan.controller.hq.storeManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.saladMan.dto.store.StoreDto;
import com.kosta.saladMan.dto.store.StoreUpdateDto;
import com.kosta.saladMan.entity.store.Store;
import com.kosta.saladMan.service.store.StoreManagementService;
import com.kosta.saladMan.service.user.StoreService;

@RestController
@RequestMapping("/hq")
public class HqStoreManageController {

	@Autowired
	private StoreManagementService hqStoreManagementService;

	@PostMapping("/storeRegister")
	public ResponseEntity<Boolean> storeRegister(@RequestBody StoreDto storeDto) {
		try {
			hqStoreManagementService.storeRegister(storeDto);
			return new ResponseEntity<>(true, HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
		}

	}

	@GetMapping("/checkStorename")
	public ResponseEntity<Boolean> checkStorename(@RequestParam String name) {
		try {
			Boolean isDuplicated = hqStoreManagementService.isStoreNameDouble(name);
			return ResponseEntity.ok(isDuplicated);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(true); // 에러 시 true로 처리
		}
	}

	@GetMapping("/checkUsername")
	public ResponseEntity<Boolean> checkUsername(@RequestParam String username) {
		try {
			Boolean isDuplicated = hqStoreManagementService.isStoreUsernameDouble(username);
			return ResponseEntity.ok(isDuplicated);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(true); // 에러 시 true로 처리
		}
	}

	@GetMapping("/storeAccountList")
	public ResponseEntity<Page<StoreDto>> getStoreAccountList(@RequestParam(required = false, defaultValue = "전체 지역") String location,
			@RequestParam(required = false) String status, @RequestParam(required = false) String keyword,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		Page<StoreDto> storePage  = hqStoreManagementService.searchStores(location, status, keyword, pageable);
		return ResponseEntity.ok(storePage);
	
	}
	
	@GetMapping("/storeAccountDetail")
	public ResponseEntity<StoreDto> getStoreAccountDetail(@RequestParam Integer id){
		
		try {
	        StoreDto store = hqStoreManagementService.getStoreDetail(id);
	        return ResponseEntity.ok(store);			
		} catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}	
		
	}
	
	@PostMapping("/storeUpdate")
	public ResponseEntity<Boolean> updateStore(@RequestBody StoreUpdateDto storeUpdateDto){
		try {
			boolean result = hqStoreManagementService.updateStore(storeUpdateDto);
			return new ResponseEntity<>(result, HttpStatus.OK);
		}catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
	}
	

}
