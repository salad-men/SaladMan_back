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
	
	// 전체 메뉴 조회
	@GetMapping("/totalMenu")
	public ResponseEntity<Map<String,Object>> getTotalMenu(@RequestParam(required = false) Map<String,String> param) {
		String sort = null;
		PageInfo pageInfo = new PageInfo(1);
		if(param != null) {
			if(param.get("page")!=null) {
				pageInfo.setCurPage(Integer.parseInt(param.get("page")));
			}
			sort = param.get("sort");
		}
		
	    try {
	        List<TotalMenuDto> totalMenu = menuService.getTotalMenu(pageInfo, sort);
	        Map<String, Object> res = new HashMap<>();
	        res.put("menus", totalMenu);
	        res.put("pageInfo", pageInfo);
	        res.put("sort", sort);
	        return new ResponseEntity<>(res, HttpStatus.OK);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }
	}
	
    // 전체 재료 목록 조회
    @GetMapping
    public ResponseEntity<List<IngredientDto>> getAllIngredients() {
	    try {
	    	List<IngredientDto> ingredients = menuService.getAllIngredients();
	        return ResponseEntity.ok(ingredients);
	    } catch (Exception e) {
			e.printStackTrace();
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
        
    }
	
	//레시피 조회
	@GetMapping("/recipe")
	public ResponseEntity<Map<String,Object>> getAllMenuRecipes(@RequestParam(defaultValue = "1") int page) {
		try {
	        PageInfo pageInfo = new PageInfo();
	        pageInfo.setCurPage(page);

	        List<RecipeDto> menus = menuService.getAllMenuRecipes(pageInfo);

	        Map<String, Object> result = new HashMap<>();
	        result.put("menus", menus);
	        result.put("pageInfo", pageInfo);

	        return ResponseEntity.ok(result);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }
	}

}
