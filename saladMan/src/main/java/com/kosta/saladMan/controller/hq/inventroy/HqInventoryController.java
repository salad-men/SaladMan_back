package com.kosta.saladMan.controller.hq.inventroy;


import com.kosta.saladMan.util.PageInfo;
import com.kosta.saladMan.dto.inventory.DisposalDto;
import com.kosta.saladMan.dto.inventory.HqIngredientDto;
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

    @GetMapping("/list2")
    public ResponseEntity<String> testList() {
        return ResponseEntity.ok("POST로 정상d 동작합니다!test");
    }
    
    @PostMapping("/list")
    public ResponseEntity<Map<String, Object>> list(@RequestBody Map<String, Object> param) {
        try {
            String scope = (String) param.getOrDefault("scope", "all");
            Object categoryObj = param.get("category");
            String keyword = (String) param.getOrDefault("name", "");
            int page = param.get("page") == null ? 1 : (int) param.get("page");

            Object storeObj = param.get("store");
            Integer storeId = null;
            Integer categoryId = null;

            // categoryObj → Integer categoryId 변환
            if (categoryObj != null && !"all".equals(categoryObj.toString().trim())) {
                try {
                    categoryId = Integer.valueOf(categoryObj.toString().trim());
                } catch (NumberFormatException e) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            }

            // storeObj → Integer storeId 변환
            if (storeObj != null && !"all".equals(storeObj.toString().trim())) {
                try {
                    storeId = Integer.valueOf(storeObj.toString().trim());
                } catch (NumberFormatException e) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            }

            PageInfo pageInfo = new PageInfo(page);
            Map<String, Object> res = new HashMap<>();

            // HQ(본사) 조회
            if ("hq".equalsIgnoreCase(scope) || "all".equalsIgnoreCase(scope) || (storeId != null && storeId == 1)) {
                List<HqIngredientDto> hqList = inventoryService.getHqInventory(storeId, categoryId, keyword, null, null, pageInfo);
                res.put("hqInventory", hqList);
            }

            // 매장 재고 조회
            if ("store".equalsIgnoreCase(scope) || "all".equalsIgnoreCase(scope)) {
                List<StoreIngredientDto> storeList;
                if (storeId == null) {
                    storeList = inventoryService.getAllStoreInventory(categoryId, keyword, null, null, pageInfo);
                } else if (storeId != 1) {
                    storeList = inventoryService.getStoreInventory(storeId, categoryId, keyword, null, null, pageInfo);
                } else {
                    storeList = List.of();
                }
                res.put("storeInventory", storeList);
            }

            res.put("pageInfo", pageInfo);
            return new ResponseEntity<>(res, HttpStatus.OK);
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
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
        	e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    
    //카테고리 조회
    @GetMapping("/categories")
    public ResponseEntity<Map<String, Object>> categories() {
        return ResponseEntity.ok(Map.of(
            "categories", inventoryService.getAllCategories()
        ));
    }

    //매장 조회(추후 변경)
    @GetMapping("/stores")
    public ResponseEntity<Map<String, Object>> stores() {
        return ResponseEntity.ok(Map.of(
            "stores", inventoryService.getAllStores()
        ));
    }
    
    //재료 조회
    @GetMapping("/ingredients")
    public ResponseEntity<Map<String, Object>> ingredients() {
        List<IngredientDto> list = inventoryService.getAllIngredients();
        return ResponseEntity.ok(Map.of("ingredients", list));
    }
    
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

    // 저장 (신규 또는 수정)
    @PostMapping("/settings-save")
    public ResponseEntity<StoreIngredientSettingDto> saveSetting(@RequestBody StoreIngredientSettingDto dto) {
        StoreIngredientSettingDto savedDto = inventoryService.saveSetting(dto);
        return ResponseEntity.ok(savedDto);
    }
    
    
}
