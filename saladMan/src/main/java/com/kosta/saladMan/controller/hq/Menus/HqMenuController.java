package com.kosta.saladMan.controller.hq.Menus;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.saladMan.dto.menu.TotalMenuDto;
import com.kosta.saladMan.service.hq.HqMenuService;

@RestController
@RequestMapping("/hq")
public class HqMenuController {
	
	@Autowired
	private HqMenuService hqMenuService;
	
	// 전체 메뉴 조회
	@GetMapping("/allMenus")
	public ResponseEntity<List<TotalMenuDto>> getAllMenus(@RequestParam(required = false) String sort) {
	    try {
	        List<TotalMenuDto> menus = hqMenuService.getAllMenus(sort);
	        return new ResponseEntity<>(menus, HttpStatus.OK);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }
	}

}
