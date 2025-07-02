package com.kosta.saladMan.controller.store.menu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.saladMan.auth.PrincipalDetails;
import com.kosta.saladMan.dto.menu.MenuToggleDto;
import com.kosta.saladMan.dto.menu.RecipeDto;
import com.kosta.saladMan.dto.menu.StoreMenuStatusDto;
import com.kosta.saladMan.dto.menu.TotalMenuDto;
import com.kosta.saladMan.entity.store.Store;
import com.kosta.saladMan.service.menu.StoreMenuService;
import com.kosta.saladMan.util.PageInfo;

@RestController
@RequestMapping("/store")
public class SMenuController {
	
	@Autowired
	private StoreMenuService menuService;
	
	// 판매 메뉴 관리
	@GetMapping("/menuStatus")
	public ResponseEntity<List<StoreMenuStatusDto>> getMenus(@AuthenticationPrincipal PrincipalDetails principalDetails) {
	    Store store = principalDetails.getStore();
	    Integer storeId = store.getId();
	    try {
	    	List<StoreMenuStatusDto> menuList = menuService.getMenuStatus(storeId);
	    	return new ResponseEntity<>(menuList, HttpStatus.OK);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }   
	}
	
	@PatchMapping("/menuStatus/toggle")
    public ResponseEntity<Boolean> toggleMenuStatus(@RequestBody MenuToggleDto menuToggle,
                                                     @AuthenticationPrincipal PrincipalDetails principalDetails) {
		try {
			Integer storeId = principalDetails.getStore().getId();
			boolean newStatus = menuService.toggleMenuStatus(storeId, menuToggle.getMenuId());
			
	        return new ResponseEntity<>(newStatus, HttpStatus.OK);
		} catch (Exception e) {
	        e.printStackTrace();
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    } 
    }
	
}
