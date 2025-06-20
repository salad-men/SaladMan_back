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
            String category = (String) param.getOrDefault("category", "all");
            String name = (String) param.getOrDefault("name", "");
            int page = param.get("page") == null ? 1 : (int) param.get("page");


            Object storeObj = param.get("store");
            System.out.println("storeObj = " + storeObj + " (" + (storeObj != null ? storeObj.getClass().getName() : "null") + ")");
            Integer storeId = null;

            try {
                if (storeObj != null && !"all".equals(storeObj.toString().trim())) {
                    storeId = Integer.valueOf(storeObj.toString().trim());
                }
            } catch (NumberFormatException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            

            PageInfo pageInfo = new PageInfo(page);
            Map<String, Object> res = new HashMap<>();

            // HQ(본사) 조회 (storeId==null 또는 1)
            if ("hq".equalsIgnoreCase(scope) || "all".equalsIgnoreCase(scope) || (storeId != null && storeId == 1)) {
                List<HqIngredientDto> hqList = inventoryService.getHqInventory(storeId, category, name, null, null, pageInfo);
                res.put("hqInventory", hqList);
            }
            
            // 매장 재고 조회
            if ("store".equalsIgnoreCase(scope) || "all".equalsIgnoreCase(scope)) {
                List<StoreIngredientDto> storeList;
                if (storeId == null) {
                    // storeId == null 이면 전체 지점 재고 조회 메서드 호출 (아래 메서드는 별도로 구현 필요)
                    storeList = inventoryService.getAllStoreInventory(category, name, null, null, pageInfo);
                } else if (storeId != 1) {
                    // 특정 지점 재고 조회
                    storeList = inventoryService.getStoreInventory(storeId, category, name, null, null, pageInfo);
                } else {
                    // 본사(storeId == 1)일 경우는 무시 또는 빈 리스트
                    storeList = List.of();
                }
                res.put("storeInventory", storeList);
            }
            res.put("pageInfo", pageInfo);
            System.out.println("🔍 scope = " + scope + ", storeId = " + storeId);

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
    
    // 매장별 설정 리스트 조회
    @GetMapping("/settings")
    public ResponseEntity<List<StoreIngredientSettingDto>> getSettings(@RequestParam Integer storeId) {
        List<StoreIngredientSettingDto> list = inventoryService.getSettingsByStoreId(storeId);
        return ResponseEntity.ok(list);
    }

    // 저장 (신규 또는 수정)
    @PostMapping("/settings-save")
    public ResponseEntity<StoreIngredientSettingDto> saveSetting(@RequestBody StoreIngredientSettingDto dto) {
        StoreIngredientSettingDto savedDto = inventoryService.saveSetting(dto);
        return ResponseEntity.ok(savedDto);
    }
    
    
}
