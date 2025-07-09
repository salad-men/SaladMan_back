package com.kosta.saladMan.controller.hq.inventroy;


import com.kosta.saladMan.util.PageInfo;
import com.kosta.saladMan.dto.inventory.HqIngredientDto;
import com.kosta.saladMan.dto.inventory.IngredientCategoryDto;
import com.kosta.saladMan.dto.inventory.IngredientDto;
import com.kosta.saladMan.dto.inventory.StoreIngredientDto;
import com.kosta.saladMan.dto.inventory.StoreIngredientSettingDto;
import com.kosta.saladMan.service.inventory.InventoryService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hq/inventory")
@RequiredArgsConstructor
public class HqInventoryController {


    private final InventoryService inventoryService;

    @PostMapping("/list")
    public ResponseEntity<Map<String, Object>> list(@RequestBody Map<String, Object> param) {
    	  try {
    	        String scope = (String) param.getOrDefault("scope", "all");
    	        Object categoryObj = param.get("category");
    	        String keyword = (String) param.getOrDefault("keyword", "");
    	        int page = param.get("page") == null ? 1 : (int) param.get("page");

    	        Object storeObj = param.get("store");
    	        Integer storeId = null;
    	        Integer categoryId = null;

    	        String startDate = (String) param.getOrDefault("startDate", null);
    	        String endDate = (String) param.getOrDefault("endDate", null);
    	        String sortOption = (String) param.getOrDefault("sortOption", "default");

    	        if (categoryObj != null && !"all".equals(categoryObj.toString().trim())) {
    	            categoryId = Integer.valueOf(categoryObj.toString().trim());
    	        }
    	        if (storeObj != null && !"all".equals(storeObj.toString().trim())) {
    	            storeId = Integer.valueOf(storeObj.toString().trim());
    	        }

    	        PageInfo pageInfo = new PageInfo(page);
    	        Map<String, Object> res = new HashMap<>();

    	        if ("hq".equalsIgnoreCase(scope) || "all".equalsIgnoreCase(scope) || (storeId != null && storeId == 1)) {
    	            List<HqIngredientDto> hqInventory = inventoryService.getHqInventory(storeId, categoryId, keyword, startDate, endDate, pageInfo, sortOption);
    	            res.put("hqInventory", hqInventory);
    	        }

    	        if ("store".equalsIgnoreCase(scope) || "all".equalsIgnoreCase(scope)) {
    	            List<StoreIngredientDto> storeList;
    	            if (storeId == null) {
    	                storeList = inventoryService.getAllStoreInventory(categoryId, keyword, startDate, endDate, pageInfo, sortOption);
    	            } else if (storeId != 1) {
    	                storeList = inventoryService.getStoreInventory(storeId, categoryId, keyword, startDate, endDate, pageInfo, sortOption);
    	            } else {
    	                storeList = List.of();
    	            }
    	            res.put("storeInventory", storeList);
    	        }

    	        res.put("pageInfo", pageInfo);
    	        return ResponseEntity.ok(res);
    	    } catch (Exception e) {
    	        e.printStackTrace();
    	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    	    }
    }



    //추가
    @PostMapping("/add")
    public ResponseEntity<Void> add(@RequestBody HqIngredientDto dto) {
        try {
            inventoryService.addHqIngredient(dto);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
        	e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<Void> update(@RequestBody HqIngredientDto dto) {
        try {
            inventoryService.updateHqIngredient(dto);
            System.out.println(dto); 
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
        	e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //매장 조회(추후 변경)
    @GetMapping("/stores")
    public ResponseEntity<Map<String, Object>> stores() {
        return ResponseEntity.ok(Map.of(
            "stores", inventoryService.getAllStores()
        ));
    }

    //재료 설정
    @GetMapping("/settings")
    public ResponseEntity<Map<String, Object>> getSettings(
        @RequestParam Integer storeId,
        @RequestParam(required = false) Integer categoryId,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false, defaultValue = "1") int page
    ) {
        PageInfo pageInfo = new PageInfo(page);
        Map<String, Object> res = new HashMap<>();

        if (storeId == 1) {  // 본사
            List<StoreIngredientSettingDto> hqSettings =
                inventoryService.getHqSettingsByFilters(storeId, categoryId, keyword, pageInfo);
            res.put("settings", hqSettings);
        } else {  // 매장
            List<StoreIngredientSettingDto> storeSettings =
                inventoryService.getStoreSettingsByFilters(storeId, categoryId, keyword, pageInfo);
            res.put("settings", storeSettings);
        }

        res.put("pageInfo", pageInfo);
        return ResponseEntity.ok(res);
    }

    // 여러 건 수정
    @PostMapping("/settings-update")
    public ResponseEntity<Void> updateSettings(@RequestBody List<StoreIngredientSettingDto> dtos) {
        for (StoreIngredientSettingDto dto : dtos) {
            // 반드시 id 있는 경우만!
            if (dto.getId() == null) continue;
            inventoryService.updateSetting(dto); 
        }
        return ResponseEntity.ok().build();
    }

    // 단일 추가(id 없이 신규)
    @PostMapping("/settings-add")
    public ResponseEntity<?> addSetting(@RequestBody StoreIngredientSettingDto dto) {
        try {
            StoreIngredientSettingDto saved = inventoryService.addSetting(dto);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            // 이미 등록된 경우(또는 검증 오류)
            return ResponseEntity.status(HttpStatus.CONFLICT)
                                 .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            // 예기치 못한 서버 오류
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("error", "서버 오류가 발생했습니다."));
        }
    }


    
    
    // 카테고리 조회
    @GetMapping("/categories")
    public ResponseEntity<Map<String, Object>> getCategories() {
        List<IngredientCategoryDto> cats = inventoryService.getAllCategories();
        return ResponseEntity.ok(Map.of("categories", cats));
    }

    // 카테고리 추가
    @PostMapping("/category-add")
    public ResponseEntity<Map<String, Object>> addCategory(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        IngredientCategoryDto dto = inventoryService.addCategory(name);
        return ResponseEntity.ok(Map.of(
            "id",   dto.getId(),
            "name", dto.getName()
        ));
    }

    // 카테고리 수정
    @PostMapping("/category-update")
    public ResponseEntity<Void> updateCategory(@RequestBody Map<String, Object> body) {
        Integer id   = (Integer) body.get("id");
        String  name = (String)  body.get("name");
        inventoryService.updateCategory(id, name);
        return ResponseEntity.ok().build();
    }
    
    // 카테고리 삭제
    @PostMapping("/category-delete")
    public ResponseEntity<Void> deleteCategory(@RequestBody Map<String, Object> body) {
        Integer id = (Integer) body.get("id");
        inventoryService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }

    // 재료 조회
    @GetMapping("/ingredients")
    public ResponseEntity<Map<String, Object>> getIngredients() {
        List<IngredientDto> list = inventoryService.getAllIngredients();
        return ResponseEntity.ok(Map.of("ingredients", list));
    }

    // 재료 추가
    @PostMapping("/ingredient-add")
    public ResponseEntity<Map<String, Object>> addIngredient(@RequestBody Map<String, Object> body) {
        String name       = (String)  body.get("name");
        String unit       = (String)  body.get("unit");
        Integer categoryId= (Integer) body.get("categoryId");
        IngredientDto dto = inventoryService.addIngredient(name, categoryId, unit);
        return ResponseEntity.ok(Map.of(
            "id",         dto.getId(),
            "name",       dto.getName(),
            "unit",       dto.getUnit(),
            "categoryId", dto.getCategoryId()
        ));
    }

    // 재료 수정
    @PostMapping("/ingredient-update")
    public ResponseEntity<Void> updateIngredient(@RequestBody Map<String, Object> body) {
        Integer id       = (Integer) body.get("id");
        String  name     = (String)  body.get("name");
        String  unit     = (String)  body.get("unit");
        inventoryService.updateIngredient(id, name, unit);
        return ResponseEntity.ok().build();
    }
    


    //재료 삭제
    @PostMapping("/ingredient-delete")
    public ResponseEntity<Void> deleteIngredient(@RequestBody Map<String, Object> body) {
        Integer id = (Integer) body.get("id");
        inventoryService.deleteIngredient(id);
        return ResponseEntity.ok().build();
    }
    
 // 재료 설정 삭제
    @PostMapping("/settings-delete")
    public ResponseEntity<Void> deleteSetting(@RequestBody Map<String, Integer> body) {
        Integer id = body.get("id");
        inventoryService.deleteSetting(id);
        return ResponseEntity.ok().build();
    }
    
    
}
