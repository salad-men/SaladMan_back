package com.kosta.saladMan.controller.hq.dashboard;

import com.kosta.saladMan.dto.dashboard.DashboardSummaryDto;
import com.kosta.saladMan.dto.dashboard.DisposalSummaryDto;
import com.kosta.saladMan.dto.dashboard.OrderSummaryDto;
import com.kosta.saladMan.dto.dashboard.StoreDashboardSummaryDto;
import com.kosta.saladMan.dto.inventory.HqIngredientDto;
import com.kosta.saladMan.dto.store.ScheduleDto;
import com.kosta.saladMan.repository.empolyee.EmployeeDslRepository;
import com.kosta.saladMan.service.dashboard.DashboardService;
import com.kosta.saladMan.service.inventory.InventoryService;
import com.kosta.saladMan.service.saleOrder.SalesService;
import com.kosta.saladMan.util.PageInfo;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
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
    private final EmployeeDslRepository employeeDslRepository; 

    @GetMapping("/summary")
    public DashboardSummaryDto getDashboardSummary(
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate,
        @RequestParam(required = false, defaultValue = "day") String groupType 

    ) {
        // 서비스에서 집계 데이터 리턴
        return dashboardService.getSummary(startDate, endDate, groupType);
    }
    
    
    @GetMapping("/summary/store")
    public StoreDashboardSummaryDto getStoreSummary(
        @RequestParam Integer storeId,
        @RequestParam String startDate,
        @RequestParam String endDate,
        @RequestParam(required = false, defaultValue = "day") String groupType,
        @RequestParam(required = false, defaultValue = "0") int weekNo
    ) {
        return dashboardService.getStoreSummary(storeId, startDate, endDate, groupType, weekNo);
    }

    /** 
     * 재고 부족 품목(minQuantity 이하)
     */
    @GetMapping("/low-stock")
    public List<HqIngredientDto> getLowStockList() {
        Integer hqStoreId = 1;
        return inventoryService.getHqInventory(
                hqStoreId, 
                null, 
                null, 
                null, 
                null, 
                new PageInfo(1), 
                null 
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

//    /**
//     * 발주 집계 (지점 수, 전체 건수 등)
//     */
//    @GetMapping("/order-summary")
//    public OrderSummaryDto getOrderSummary(
//            @RequestParam(required = false) String startDate,
//            @RequestParam(required = false) String endDate
//    ) {
//        return salesService.getOrderSummaryTop3WithCountMerged(startDate, endDate);
//    }
//
//    /**
//     * 폐기 집계 (지점 수, 전체 건수 등)
//     */
//    @GetMapping("/disposal-summary")
//    public DisposalSummaryDto getDisposalSummary(
//            @RequestParam(required = false) String startDate,
//            @RequestParam(required = false) String endDate
//    ) {
//        return inventoryService.getDisposalSummaryTop3WithCountMerged(startDate, endDate);
//    }

}
