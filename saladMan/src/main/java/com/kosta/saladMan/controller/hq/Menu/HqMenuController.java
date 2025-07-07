package com.kosta.saladMan.controller.hq.Menu;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
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
	
	@PostMapping("/registerMenu")
    public ResponseEntity<?> registerMenu(
            @RequestPart("menu") String menuJson,
            @RequestPart(value = "image", required = false) MultipartFile imageFile
    ) {
        try {
            MenuRegisterDto dto = objectMapper.readValue(menuJson, MenuRegisterDto.class);
            menuService.registerMenu(dto, imageFile);
            return ResponseEntity.ok("메뉴 등록 성공");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("메뉴 등록 실패");
        }
    }
	
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
