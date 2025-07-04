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
            System.out.println(">>> getDisposalList 요청 파라미터: " + params);

        	Object storeParamObj = params.getOrDefault("store", "all");
        	Integer storeId = null;
        	
	       if (storeParamObj instanceof Number) {
		           storeId = ((Number) storeParamObj).intValue();
		       }
		       // 2) 문자열 "all", "null" 이나 공백일 때는 그대로 null
		       else if (storeParamObj instanceof String) {
		           String sp = ((String) storeParamObj).trim();
		           if (!sp.isEmpty() && !"all".equalsIgnoreCase(sp) && !"null".equalsIgnoreCase(sp)) {
		               try {
		                   storeId = Integer.valueOf(sp);
		               } catch (NumberFormatException e) {
		                   storeId = null;
		               }
		           }
	       }
        	
        	Object categoryObj = params.get("category");
        	Integer categoryId = null;
        	if (categoryObj instanceof Number) {
        	    categoryId = ((Number) categoryObj).intValue();
        	} else if (categoryObj instanceof String) {
        	    String cp = ((String) categoryObj).trim();
        	    if (!cp.isEmpty() && !"all".equalsIgnoreCase(cp) && !"null".equalsIgnoreCase(cp)) {
        	        try {
        	            categoryId = Integer.valueOf(cp);
        	        } catch (NumberFormatException e) {
        	            categoryId = null;
        	        }
        	    }
        	}



            String startDate = (String) params.getOrDefault("startDate", "");
            String endDate = (String) params.getOrDefault("endDate", "");

            String keyword = (String) params.getOrDefault("keyword", "");
            int page = 1;
            Object pageObj = params.get("page");
            if (pageObj instanceof Number) {
                page = ((Number) pageObj).intValue();
            }

            PageInfo pageInfo = new PageInfo(page);

            String status = (String) params.getOrDefault("status", "all");
            String sortOption = (String) params.getOrDefault("sortOption", "dateDesc");

            List<DisposalDto> list;

            if (storeId == null) { // "all" 인 경우
                // 본사 제외 모든 매장 폐기 목록 조회
                list = inventoryService.searchAllStoresExceptHqDisposals(
                        pageInfo,
                        categoryId,
                        status,
                        startDate,
                        endDate,
                        sortOption,
                        keyword
                );
            } else if (storeId == 1) {
                // 본사 폐기 목록 조회
                list = inventoryService.searchHqDisposals(
                        pageInfo,
                        categoryId,
                        status,
                        startDate,
                        endDate,
                        sortOption,
                        keyword
                );
            } else {
                // 특정 매장 폐기 목록 조회
                list = inventoryService.searchStoreDisposals(
                        pageInfo,
                        storeId,
                        categoryId,
                        status,
                        startDate,
                        endDate,
                        sortOption,
                        keyword
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
        if (disposalIds == null || disposalIds.isEmpty() || disposalIds.contains(null)) {
            return ResponseEntity.badRequest().build();
        }
    	try {
            System.out.println("approveDisposals ids: " + disposalIds);
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
