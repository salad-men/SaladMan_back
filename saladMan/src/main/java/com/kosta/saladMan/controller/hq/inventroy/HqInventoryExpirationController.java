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
    public ResponseEntity<Map<String,Object>> getExpirationInventory(@RequestBody Map<String,Object> param) {
        try {
            String store = (String) param.getOrDefault("store", "all");
            String category = (String) param.getOrDefault("category", "");
            if ("all".equalsIgnoreCase(category)) category = "";

            String keyword = (String) param.getOrDefault("keyword", "");
            String startDate = (String) param.getOrDefault("startDate", "");
            String endDate = (String) param.getOrDefault("endDate", "");
            int page = param.get("page") == null ? 1 : (int) param.get("page");

            PageInfo pageInfo = new PageInfo(page);

            Map<String,Object> res = new HashMap<>();

            if ("본사".equalsIgnoreCase(store)) {
                List<HqIngredientDto> hqList = inventoryService.searchHqInventory(pageInfo, category, keyword, startDate, endDate);
                res.put("hqInventory", hqList);
            } else if ("all".equalsIgnoreCase(store)) {
                List<HqIngredientDto> hqList = inventoryService.searchHqInventory(pageInfo, category, keyword, startDate, endDate);
                List<StoreIngredientDto> storeList = inventoryService.searchStoreInventory(pageInfo, store, category, keyword, startDate, endDate);
                res.put("hqInventory", hqList);
                res.put("storeInventory", storeList);
            } else {
                List<StoreIngredientDto> storeList = inventoryService.searchStoreInventory(pageInfo, store, category, keyword, startDate, endDate);
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
