package com.kosta.saladMan.controller.store.kiosk;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.saladMan.auth.PrincipalDetails;
import com.kosta.saladMan.dto.menu.StoreMenuStatusDto;
import com.kosta.saladMan.entity.store.Store;
import com.kosta.saladMan.service.menu.StoreMenuService;

@RestController
@RequestMapping("/kiosk")
public class KioskController {
	
	@Autowired
	private StoreMenuService menuService;

	@GetMapping("/menus")
	public ResponseEntity<List<StoreMenuStatusDto>> getMenus(@AuthenticationPrincipal PrincipalDetails principalDetails) {
	    Store store = principalDetails.getStore();
	    Integer storeId = store.getId();
	    System.out.println(storeId);
	    try {
	    	List<StoreMenuStatusDto> menuList = menuService.getMenuStatus(storeId);
	    	return new ResponseEntity<>(menuList, HttpStatus.OK);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }   
	}
	
}
