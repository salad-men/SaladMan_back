package com.kosta.saladMan.controller.hq.storeManagement;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.saladMan.auth.PrincipalDetails;
import com.kosta.saladMan.dto.store.CloseStoreDto;
import com.kosta.saladMan.dto.store.ResetStorePasswordDto;
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
	public ResponseEntity<Map<String, Object>> storeRegister(@RequestBody StoreDto storeDto) {
	    try {
	        Integer storeId = hqStoreManagementService.storeRegister(storeDto); 
	        return ResponseEntity.ok(Map.of("success", true, "id", storeId));
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(Map.of("success", false));
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
	public ResponseEntity<Page<StoreDto>> getStoreAccountList(
			@RequestParam(required = false, defaultValue = "전체 지역") String location,
			@RequestParam(required = false) String status, @RequestParam(required = false) String keyword,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		Page<StoreDto> storePage = hqStoreManagementService.searchStores(location, status, keyword, pageable);
		return ResponseEntity.ok(storePage);

	}

	@GetMapping("/storeAccountDetail")
	public ResponseEntity<StoreDto> getStoreAccountDetail(@RequestParam Integer id) {

		try {
			StoreDto store = hqStoreManagementService.getStoreDetail(id);
			return ResponseEntity.ok(store);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

	}

	@PostMapping("/storeUpdate")
	public ResponseEntity<Boolean> updateStore(@RequestBody StoreUpdateDto storeUpdateDto) {
		try {
			boolean result = hqStoreManagementService.updateStore(storeUpdateDto);
			return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/resetStorePassword")
	public ResponseEntity<Boolean> resetStorePassword(@RequestBody ResetStorePasswordDto dto,
			@AuthenticationPrincipal PrincipalDetails principal) {
		Store adminStore = principal.getStore();

		// 관리자 권한 확인
		if (!"ROLE_HQ".equals(adminStore.getRole())) {
			throw new AccessDeniedException("관리자 권한이 필요합니다.");
		}

		try {
			hqStoreManagementService.resetStorePassword(adminStore, dto);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
		}

	}

	@PostMapping("/closeStore")
	public ResponseEntity<Boolean> resetStorePassword(@RequestBody CloseStoreDto dto) {
		try {
			hqStoreManagementService.closeStore(dto);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
		}
	}

}
