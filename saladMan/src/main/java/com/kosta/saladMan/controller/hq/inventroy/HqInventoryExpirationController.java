package com.kosta.saladMan.controller.hq.inventroy;


import com.kosta.saladMan.dto.inventory.DisposalDto;
import com.kosta.saladMan.dto.inventory.HqIngredientDto;
import com.kosta.saladMan.dto.inventory.StoreIngredientDto;
import com.kosta.saladMan.service.inventory.InventoryService;
import com.kosta.saladMan.util.PageInfo;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hq/inventory")
@RequiredArgsConstructor
public class HqInventoryExpirationController {

    private final InventoryService inventoryService;

        @PostMapping("/expiration-list")
        public ResponseEntity<Map<String, Object>> getExpirationInventory(@RequestBody Map<String, Object> param) {
            try {
                String scope = (String) param.getOrDefault("scope", "all");
                String category = (String) param.getOrDefault("category", "all");
                String storeParam = (String) param.getOrDefault("store", "all");
                String name = (String) param.getOrDefault("name", "");

                String keyword = (String) param.getOrDefault("keyword", "");
                String startDate = (String) param.getOrDefault("startDate", "");
                String endDate = (String) param.getOrDefault("endDate", "");
                int page = param.get("page") == null ? 1 : (int) param.get("page");
                Object storeObj = param.get("store");



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

                //본사 조회
                if ("hq".equalsIgnoreCase(scope) || "all".equalsIgnoreCase(scope) || (storeId != null && storeId == 1)) {
                    List<HqIngredientDto> hqInventory = inventoryService.getHqInventory(storeId, category, keyword, startDate, endDate, pageInfo);
                    res.put("hqInventory", hqInventory);
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
                return ResponseEntity.ok(res);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        @PostMapping("/disposal-request")
        public ResponseEntity<Void> requestDisposal(@RequestBody List<DisposalDto> disposalList) {
            try {
                inventoryService.processDisposalRequest(disposalList);
                return new ResponseEntity<>(HttpStatus.CREATED);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
}
