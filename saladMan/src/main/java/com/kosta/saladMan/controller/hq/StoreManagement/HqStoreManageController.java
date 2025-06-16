package com.kosta.saladMan.controller.hq.StoreManagement;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.kosta.saladMan.entity.store.Store;
import com.kosta.saladMan.service.hq.StoreManagement.HqStoreManagementService;

 
@RestController
@RequestMapping("/hq")
public class HqStoreManageController {
	
	@Autowired
	private HqStoreManagementService hqStoreManagementService;
	

	
	@PostMapping("/storeRegister")
	public ResponseEntity<Boolean> storeRegister(@RequestBody StoreDto storeDto){
		try {
			hqStoreManagementService.storeRegister(storeDto);
			return new ResponseEntity<>(true,HttpStatus.OK);
		
		} catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
		}

	}
	
	@GetMapping("/checkStorename")
	public ResponseEntity<Boolean> checkStorename(@RequestParam String name){
        try {
            Boolean isDuplicated = hqStoreManagementService.isStoreNameDouble(name);
            return ResponseEntity.ok(isDuplicated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(true); // 에러 시 true로 처리
        }		
	}
	
	@GetMapping("/checkUsername")
	public ResponseEntity<Boolean> checkUsername(@RequestParam String username){
        try {
            Boolean isDuplicated = hqStoreManagementService.isStoreUsernameDouble(username);
            return ResponseEntity.ok(isDuplicated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(true); // 에러 시 true로 처리
        }		
	}	

}
