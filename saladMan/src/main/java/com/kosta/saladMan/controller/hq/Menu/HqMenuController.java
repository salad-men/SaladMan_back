package com.kosta.saladMan.controller.hq.Menu;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosta.saladMan.dto.menu.IngredientInfoDto;
import com.kosta.saladMan.dto.menu.MenuCategoryDto;
import com.kosta.saladMan.dto.menu.MenuRegisterDto;
import com.kosta.saladMan.dto.menu.TotalMenuDto;
import com.kosta.saladMan.service.menu.StoreMenuService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hq")
public class HqMenuController {
	
	private final StoreMenuService menuService;
	private final ObjectMapper objectMapper;
	
//	@PostMapping("/registerMenu")
//    public ResponseEntity<?> registerMenu(
//            @RequestParam("menu") String menuJson,
//            @RequestParam(value = "image", required = false) MultipartFile imageFile) throws Exception {
//
//        MenuRegisterDto dto = objectMapper.readValue(menuJson, MenuRegisterDto.class);
//
//        Long id = menuService.saveMenu(dto, imageFile);
//
//        return ResponseEntity.ok("등록 완료, 메뉴 ID: " + id);
//    }
	
	@GetMapping("/ingredientInfo")
	public ResponseEntity<List<IngredientInfoDto>> getIngredients() {
	    try {
			return new ResponseEntity<>(menuService.getIngredientInfo(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/menuCategory")
	public ResponseEntity<List<MenuCategoryDto>> getMenuCategory() {
		try {
			return new ResponseEntity<>(menuService.getMenuCategory(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

}
