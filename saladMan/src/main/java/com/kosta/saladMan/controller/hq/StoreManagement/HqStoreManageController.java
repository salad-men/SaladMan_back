package com.kosta.saladMan.controller.hq.StoreManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.saladMan.dto.store.StoreDto;
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

}
