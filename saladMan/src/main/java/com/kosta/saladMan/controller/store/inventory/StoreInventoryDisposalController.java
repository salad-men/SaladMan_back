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
        Integer storeId = (Integer) params.get("storeId");
        
        Object categoryObj = params.get("category");
        Integer categoryId = null;

        if (categoryObj != null && !"all".equals(categoryObj.toString())) {
            categoryId = Integer.parseInt(categoryObj.toString());
        }
        
        String keyword = (String) params.getOrDefault("keyword", "");
        String startDate = (String) params.getOrDefault("startDate", "");
        String endDate = (String) params.getOrDefault("endDate", "");
        int page = params.get("page") == null ? 1 : (int) params.get("page");

        PageInfo pageInfo = new PageInfo();
        pageInfo.setCurPage(page);
        
        Object pageObj = params.get("page");
        if (pageObj != null) {
            page = Integer.parseInt(pageObj.toString());
        }
        

        List<DisposalDto> list = inventoryService.searchStoreDisposals(
            pageInfo,
            storeId,
            categoryId,
            keyword,
            startDate,
            endDate
        );

        Map<String, Object> result = new HashMap<>();
        result.put("disposals", list);
        result.put("pageInfo", pageInfo);
        return result;
    }
}
