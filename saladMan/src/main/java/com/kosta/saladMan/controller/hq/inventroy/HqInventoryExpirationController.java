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

 // 전체/유통기한/페이징 공통
    @PostMapping("/expiration-list")
    public ResponseEntity<Map<String, Object>> getExpirationInventory(@RequestBody Map<String, Object> param) {
        try {
            String storeParam = (String) param.getOrDefault("store", "all");
            Integer storeId = "all".equals(storeParam) ? null : Integer.valueOf(storeParam);

            String category = (String) param.getOrDefault("category", "");
            String keyword = (String) param.getOrDefault("keyword", "");
            String startDate = (String) param.getOrDefault("startDate", "");
            String endDate = (String) param.getOrDefault("endDate", "");
            int page = param.get("page") == null ? 1 : (int) param.get("page");
            PageInfo pageInfo = new PageInfo(page);

            List<HqIngredientDto> hqInventory = inventoryService.getHqInventory(storeId, category, keyword, startDate, endDate, pageInfo);
            List<StoreIngredientDto> storeInventory = inventoryService.getStoreInventory(storeId, category, keyword, startDate, endDate, pageInfo);
            
            Map<String, Object> res = new HashMap<>();
            res.put("hqInventory", hqInventory);
            res.put("storeInventory", storeInventory);
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
