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

 
@RestController
@RequestMapping("/hq")
public class HqStoreManageController {
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@PostMapping("/storeRegister")
	public ResponseEntity<Boolean> storeRegister(@RequestBody StoreDto storeDto){
		try {
			storeDto.setPassword(bCryptPasswordEncoder.encode(storeDto.getPassword()));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return new ResponseEntity<>(true,HttpStatus.OK);

	}

}
