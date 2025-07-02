package com.kosta.saladMan.controller.kiosk;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.saladMan.auth.PrincipalDetails;
import com.kosta.saladMan.dto.menu.KioskMenuDto;
import com.kosta.saladMan.dto.menu.MenuCategoryDto;
import com.kosta.saladMan.dto.menu.StoreMenuStatusDto;
import com.kosta.saladMan.entity.store.Store;
import com.kosta.saladMan.service.kiosk.KioskService;
import com.kosta.saladMan.service.menu.StoreMenuService;

@RestController
@RequestMapping("/kiosk")
public class KioskController {
	
	@Autowired
	private KioskService kioskService;

	@GetMapping("/menus")
	public ResponseEntity<Page<KioskMenuDto>>getMenus(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestParam(required = false) Integer categoryId,
		    @RequestParam(required = false) String categoryName,
		    @PageableDefault(size = 9) Pageable pageable) {
	    Store store = principalDetails.getStore();
	    Integer storeId = store.getId();
	    System.out.println(storeId);
	    try {
	    	Page<KioskMenuDto> result =  kioskService.getStoreMenuByKiosk(storeId, categoryId, categoryName, pageable);
	    	for(KioskMenuDto menu:result) {
	    		System.out.println(menu);
	    	}
	    	System.out.println(result);
	    	return new ResponseEntity<>(result,HttpStatus.OK);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }   
	}
	
	@GetMapping("/menuCategories")
	public ResponseEntity<List<MenuCategoryDto>>getMenus(){
	    try {
	    	List<MenuCategoryDto> result =kioskService.getAllCategory();
	    	return new ResponseEntity<>(result,HttpStatus.OK);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }
	}

	
}
