package com.kosta.saladMan.controller.store.inventory;

import com.kosta.saladMan.dto.inventory.StoreIngredientDto;
import com.kosta.saladMan.dto.inventory.StoreIngredientSettingDto;
import com.kosta.saladMan.dto.inventory.IngredientDto;
import com.kosta.saladMan.dto.inventory.IngredientCategoryDto;
import com.kosta.saladMan.dto.store.StoreDto;
import com.kosta.saladMan.service.inventory.InventoryService;
import com.kosta.saladMan.util.PageInfo;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/store/inventory")
@RequiredArgsConstructor
public class StoreInventoryController {

    private final InventoryService inventoryService;

    // 1. 매장 재고 리스트 조회
    @PostMapping("/list")
    public ResponseEntity<Map<String, Object>> list(@RequestBody Map<String, Object> param) {
        try {
            Integer storeId = param.get("storeId") == null ? null : Integer.valueOf(param.get("storeId").toString());
            if (storeId == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            Integer categoryId = null;
            if (param.get("category") != null && !"all".equals(param.get("category").toString())) {
                categoryId = Integer.valueOf(param.get("category").toString());
            }
            String keyword = (String) param.getOrDefault("name", "");
            int page = param.get("page") == null ? 1 : (int) param.get("page");

            String startDate = (String) param.getOrDefault("startDate", null);
            String endDate = (String) param.getOrDefault("endDate", null);
            String sortOption = (String) param.getOrDefault("sortOption", "default");

            PageInfo pageInfo = new PageInfo(page);

            List<StoreIngredientDto> storeInventory = inventoryService.getStoreInventory(storeId, categoryId, keyword, startDate, endDate, pageInfo, sortOption);

            Map<String, Object> res = new HashMap<>();
            res.put("storeInventory", storeInventory);
            res.put("pageInfo", pageInfo);

            return ResponseEntity.ok(res);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    // 2. 재고 수정
    @PostMapping("/update")
    public ResponseEntity<Void> update(@RequestBody StoreIngredientDto dto) {
        try {
            inventoryService.updateStoreIngredient(dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // 3. 카테고리 조회
    @GetMapping("/categories")
    public ResponseEntity<Map<String, Object>> categories() {
        try {
            List<IngredientCategoryDto> categories = inventoryService.getAllCategories();
            return ResponseEntity.ok(Map.of("categories", categories));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // 4. 매장 조회
    @GetMapping("/stores")
    public ResponseEntity<Map<String, Object>> stores() {
        try {
            List<StoreDto> stores = inventoryService.getStoresExceptHQ(); // 본사 제외 매장만 조회
            return ResponseEntity.ok(Map.of("stores", stores));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // 5. 재료 조회
    @GetMapping("/ingredients")
    public ResponseEntity<Map<String, Object>> ingredients() {
        try {
            List<IngredientDto> ingredients = inventoryService.getAllIngredients();
            return ResponseEntity.ok(Map.of("ingredients", ingredients));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // 6. 재료 설정 조회
    @GetMapping("/settings")
    public ResponseEntity<Map<String, Object>> getSettings(
            @RequestParam Integer storeId,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "1") int page) {
        try {
            PageInfo pageInfo = new PageInfo(page);
            List<StoreIngredientSettingDto> settings = inventoryService.getStoreSettingsByFilters(storeId, categoryId, keyword, pageInfo);

            Map<String, Object> res = new HashMap<>();
            res.put("settings", settings);
            res.put("pageInfo", pageInfo);

            return ResponseEntity.ok(res);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // 여러 건 수정(배열, id 必)
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
    public ResponseEntity<StoreIngredientSettingDto> addSetting(@RequestBody StoreIngredientSettingDto dto) {
        StoreIngredientSettingDto saved = inventoryService.addSetting(dto);
        return ResponseEntity.ok(saved);
    }
    
    @PostMapping("/add")
    public ResponseEntity<Void> add(@RequestBody StoreIngredientDto dto) {
        try {
            inventoryService.addStoreIngredient(dto); 
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


}
