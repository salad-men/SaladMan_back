package com.kosta.saladMan.controller.hq.Menu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.saladMan.dto.inventory.IngredientDto;
import com.kosta.saladMan.dto.menu.IngredientInfoDto;
import com.kosta.saladMan.dto.menu.RecipeDto;
import com.kosta.saladMan.dto.menu.TotalMenuDto;
import com.kosta.saladMan.service.inventory.InventoryService;
import com.kosta.saladMan.service.menu.StoreMenuService;
import com.kosta.saladMan.service.notice.ComplaintService;
import com.kosta.saladMan.service.user.MenuService;
import com.kosta.saladMan.util.PageInfo;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hq")
public class HqMenuController {
	
	private final StoreMenuService menuService;
	
	@GetMapping("/ingredientInfo")
	public ResponseEntity<List<IngredientInfoDto>> getIngredients() {
	    try {
			return new ResponseEntity<>(menuService.getIngredientInfo(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

}
