package com.kosta.saladMan.controller.hq.inventroy;


import com.kosta.saladMan.dto.inventory.DisposalDto;
import com.kosta.saladMan.service.inventory.InventoryService;
import com.kosta.saladMan.util.PageInfo;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hq/inventory")
@RequiredArgsConstructor
public class HqInventoryDisposalController {

    private final InventoryService inventoryService; // DisposalService → InventoryService 변경

    @PostMapping("/disposal-list")
    public ResponseEntity<Map<String, Object>> getDisposalList(@RequestBody Map<String, Object> params) {
        try {
            
            String storeParam    = (String) params.getOrDefault("store", "all");
            Integer storeId      = !"all".equals(storeParam)
                                   ? Integer.valueOf(storeParam)
                                   : null;

            String categoryParam = (String) params.getOrDefault("category", "all");
            Integer categoryId   = !"all".equals(categoryParam)
                                   ? Integer.valueOf(categoryParam)
                                   : null;

            String startDate     = (String) params.getOrDefault("startDate", "");
            String endDate       = (String) params.getOrDefault("endDate", "");
            
            int page = 1;
            Object pageObj = params.get("page");
            if (pageObj instanceof Number) {
                page = ((Number) pageObj).intValue();
            }

            
            PageInfo pageInfo    = new PageInfo(page);

            String status      = (String) params.getOrDefault("status", "all");       
            String sortOption  = (String) params.getOrDefault("sortOption", "dateDesc"); 

            List<DisposalDto> list;
            if (storeId == null || storeId == 1) {
                list = inventoryService.searchHqDisposals(
                    pageInfo,
                    categoryId,
                    status,        
                    startDate,
                    endDate,
                    sortOption     
                );
            } else {
                list = inventoryService.searchStoreDisposals(
                    pageInfo,
                    storeId,
                    categoryId,
                    status,        
                    startDate,
                    endDate,
                    sortOption     
                );
            }

            return ResponseEntity.ok(Map.of(
                "disposals", list,
                "pageInfo", pageInfo
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // 폐기 승인 (상태 '완료'로 변경)
    @PostMapping("/disposal/approve")
    public ResponseEntity<Void> approveDisposals(@RequestBody List<Integer> disposalIds) {
        try {
            inventoryService.approveDisposals(disposalIds);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // 폐기 반려 (상태 '반려됨' + 사유 저장)
    @PostMapping("/disposal/reject")
    public ResponseEntity<Void> rejectDisposals(@RequestBody List<DisposalDto> rejectDtos) {
        try {
            inventoryService.rejectDisposals(rejectDtos);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
