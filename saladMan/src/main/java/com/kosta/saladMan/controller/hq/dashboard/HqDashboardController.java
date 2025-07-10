package com.kosta.saladMan.controller.hq.dashboard;

import com.kosta.saladMan.dto.dashboard.DashboardSummaryDto;
import com.kosta.saladMan.dto.dashboard.DisposalSummaryDto;
import com.kosta.saladMan.dto.dashboard.OrderSummaryDto;
import com.kosta.saladMan.dto.inventory.HqIngredientDto;
import com.kosta.saladMan.service.dashboard.DashboardService;
import com.kosta.saladMan.service.inventory.InventoryService;
import com.kosta.saladMan.service.saleOrder.SalesService;
import com.kosta.saladMan.util.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/hq/dashboard")
@RequiredArgsConstructor
public class HqDashboardController {

    private final InventoryService inventoryService;
    private final SalesService salesService;
    private final DashboardService dashboardService;
    
    @GetMapping("/summary")
    public DashboardSummaryDto getDashboardSummary(
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate
    ) {
        // 서비스에서 집계 데이터 리턴
        return dashboardService.getSummary(startDate, endDate);
    }

    /** 
     * 재고 부족 품목(minQuantity 이하)
     */
    @GetMapping("/low-stock")
    public List<HqIngredientDto> getLowStockList() {
        Integer hqStoreId = 1;
        // 필요한 경우, 'minQuantity' 이하 필터는 서비스 내부에서 추가해도 됨
        return inventoryService.getHqInventory(
                hqStoreId, 
                null, // categoryId
                null, // keyword
                null, // startDate
                null, // endDate
                new PageInfo(1), 
                null // sortOption
        );
    }

    /**
     * 유통기한 임박 품목(7일 이내)
     */
    @GetMapping("/expire-stock")
    public List<Map<String, Object>> getExpireStockList() {
        Integer hqStoreId = 1;
        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusDays(7);

        return inventoryService.getHqInventory(hqStoreId, null, null, null, null, new PageInfo(1), null)
                .stream()
                .filter(item -> item.getExpiredDate() != null
                        && !item.getExpiredDate().isBefore(today)
                        && !item.getExpiredDate().isAfter(limit)
                        && item.getQuantity() != null && item.getQuantity() > 0)
                .map(item -> {
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id", item.getId());
                    map.put("categoryName", item.getCategoryName());
                    map.put("ingredientName", item.getIngredientName());
                    map.put("quantity", item.getQuantity());
                    map.put("unit", item.getUnit());
                    map.put("expiredDate", item.getExpiredDate());
                    return map;
                })
                .collect(Collectors.toList());
    }



    /**
     * 발주 집계 (지점 수, 전체 건수 등)
     */
    @GetMapping("/order-summary")
    public OrderSummaryDto getOrderSummary(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        // null 전달 시 전체 기간, 필요시 쿼리스트링으로 날짜 지정 가능
        return salesService.getOrderSummaryTop3WithCountMerged(startDate, endDate);
    }

    /**
     * 폐기 집계 (지점 수, 전체 건수 등)
     */
    @GetMapping("/disposal-summary")
    public DisposalSummaryDto getDisposalSummary(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        return inventoryService.getDisposalSummaryTop3WithCountMerged(startDate, endDate);
    }
}
