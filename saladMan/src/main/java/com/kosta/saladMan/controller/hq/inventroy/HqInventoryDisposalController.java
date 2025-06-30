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
            Object storeObj = params.get("store");
            Object categoryObj = params.get("category");
            Integer storeId = null;
            Integer categoryId = null;

            if (storeObj != null && !"all".equals(storeObj.toString())) {
                storeId = Integer.parseInt(storeObj.toString());
            }
            if (categoryObj != null && !"all".equals(categoryObj.toString())) {
                categoryId = Integer.parseInt(categoryObj.toString());
            }

            String keyword = (String) params.getOrDefault("keyword", "");
            String startDate = (String) params.getOrDefault("startDate", "");
            String endDate = (String) params.getOrDefault("endDate", "");
            int page = params.get("page") == null ? 1 : (int) params.get("page");

            PageInfo pageInfo = new PageInfo(page);
            List<DisposalDto> disposalList;

            if (storeId != null && storeId == 1) {
                disposalList = inventoryService.searchHqDisposals(pageInfo, categoryId, keyword, startDate, endDate);
            } else {
                disposalList = inventoryService.searchStoreDisposals(pageInfo, storeId, categoryId, keyword, startDate, endDate);
            }

            return ResponseEntity.ok(Map.of(
                "disposals", disposalList,
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
