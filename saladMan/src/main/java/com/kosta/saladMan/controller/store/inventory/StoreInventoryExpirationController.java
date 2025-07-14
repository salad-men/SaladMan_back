package com.kosta.saladMan.controller.store.inventory;

import com.kosta.saladMan.dto.inventory.DisposalDto;
import com.kosta.saladMan.service.inventory.InventoryService;
import com.kosta.saladMan.util.PageInfo;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/store/inventory")
@RequiredArgsConstructor
public class StoreInventoryExpirationController {

    private final InventoryService inventoryService;

    @PostMapping("/expiration-list")
    public ResponseEntity<Map<String, Object>> getExpirationList(@RequestBody Map<String, Object> params) {
        try {
            Integer storeId = params.get("store") == null ? null : Integer.valueOf(params.get("store").toString());
            if (storeId == null) {
                return ResponseEntity.badRequest().build();
            }

            Integer categoryId = null;
            if (params.get("category") != null && !"all".equals(params.get("category").toString())) {
                categoryId = Integer.valueOf(params.get("category").toString());
            }

            String keyword = (String) params.getOrDefault("keyword", "");

            String startDate = (String) params.getOrDefault("startDate", null);
            String endDate = (String) params.getOrDefault("endDate", null);
            String sortOption = (String) params.getOrDefault("sortOption", "default");


            int page = params.get("page") == null ? 1 : (int) params.get("page");

            PageInfo pageInfo = new PageInfo(page);

            List<?> storeInventory = inventoryService.getStoreInventoryExpiration(
                    storeId, categoryId, keyword, startDate, endDate, pageInfo, sortOption
                );

            return ResponseEntity.ok(Map.of(
                "storeInventory", storeInventory,
                "pageInfo", pageInfo
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }


    @PostMapping("/disposal-request")
    public ResponseEntity<Void> disposalRequest(@RequestBody List<DisposalDto> disposalList) {
        try {
            inventoryService.processDisposalRequest(disposalList);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
