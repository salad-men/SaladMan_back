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
    
    //유통기한 조회
    @PostMapping("/expiration-list")
    public ResponseEntity<Map<String, Object>> getExpirationInventory(@RequestBody Map<String, Object> param) {
        try {
            String scope = (String) param.getOrDefault("scope", "all");
            Object categoryObj = param.get("category");
            Object storeObj = param.get("store");

            Integer categoryId = null;
            Integer storeId = null;

            if (categoryObj != null && !"all".equals(categoryObj.toString().trim())) {
                try {
                    categoryId = Integer.valueOf(categoryObj.toString().trim());
                } catch (NumberFormatException e) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            }

            if (storeObj != null && !"all".equals(storeObj.toString().trim())) {
                try {
                    storeId = Integer.valueOf(storeObj.toString().trim());
                } catch (NumberFormatException e) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            }

            String keyword = (String) param.getOrDefault("keyword", "");
            String startDate = (String) param.getOrDefault("startDate", "");
            String endDate = (String) param.getOrDefault("endDate", "");
            int page = param.get("page") == null ? 1 : (int) param.get("page");

            PageInfo pageInfo = new PageInfo(page);

            Map<String, Object> res = new HashMap<>();

            // 본사 조회
            if ("hq".equalsIgnoreCase(scope) || "all".equalsIgnoreCase(scope) || (storeId != null && storeId == 1)) {
                // categoryId, keyword, startDate, endDate를 넘겨야 합니다
                List<HqIngredientDto> hqInventory = inventoryService.getHqInventory(storeId, categoryId, keyword, startDate, endDate, pageInfo);
                res.put("hqInventory", hqInventory);
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
