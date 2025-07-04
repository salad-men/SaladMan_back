package com.kosta.saladMan.controller.store.inventory;

import com.kosta.saladMan.dto.inventory.DisposalDto;
import com.kosta.saladMan.service.inventory.InventoryService;
import com.kosta.saladMan.util.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/store/inventory")
@RequiredArgsConstructor
public class StoreInventoryDisposalController {
    private final InventoryService inventoryService;

    @PostMapping("/disposal-list")
    public Map<String, Object> getStoreDisposals(@RequestBody Map<String, Object> params) {
        // --- 필수 파라미터 ---
        Integer storeId = params.get("storeId") == null
            ? null
            : Integer.parseInt(params.get("storeId").toString());

        Object categoryObj = params.get("category");
        Integer categoryId = null;
        if (categoryObj != null && !"all".equals(categoryObj.toString())) {
            categoryId = Integer.parseInt(categoryObj.toString());
        }

        // --- 추가된 파라미터: 상태(status), 정렬(sortOption) ---
        String status     = (String) params.getOrDefault("status", "all");          
        String sortOption = (String) params.getOrDefault("sortOption", "dateDesc"); 

        String startDate = (String) params.getOrDefault("startDate", "");
        String endDate   = (String) params.getOrDefault("endDate", "");

        // --- 페이지 정보 설정 ---
        int page = params.get("page") == null
            ? 1
            : Integer.parseInt(params.get("page").toString());
        PageInfo pageInfo = new PageInfo(page);

        // --- 서비스 호출 (수정: 새 파라미터 추가) ---
        List<DisposalDto> list = inventoryService.searchStoreDisposals(
            pageInfo,
            storeId,
            categoryId,
            status,       
            startDate,
            endDate,
            sortOption    
        );

        Map<String, Object> result = new HashMap<>();
        result.put("disposals", list);
        result.put("pageInfo", pageInfo);
        return result;
    }
}
