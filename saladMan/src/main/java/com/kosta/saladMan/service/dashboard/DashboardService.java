package com.kosta.saladMan.service.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.kosta.saladMan.dto.dashboard.DashboardSummaryDto;
import com.kosta.saladMan.dto.dashboard.DisposalSummaryDto;
import com.kosta.saladMan.dto.dashboard.InventoryExpireSummaryDto;
import com.kosta.saladMan.dto.dashboard.MainStockSummaryDto;
import com.kosta.saladMan.dto.dashboard.OrderSummaryDto;
import com.kosta.saladMan.dto.dashboard.StoreDashboardSummaryDto;
import com.kosta.saladMan.dto.saleOrder.SalesResultDto;
import com.kosta.saladMan.dto.saleOrder.StoreSalesResultDto;
import com.kosta.saladMan.dto.notice.NoticeDto;
import com.kosta.saladMan.dto.saleOrder.SalesResultDto.GroupType;
import com.kosta.saladMan.dto.saleOrder.SalesResultDto.MenuSalesDto;
import com.kosta.saladMan.repository.StoreRepository;
import com.kosta.saladMan.repository.inventory.StoreInventoryDslRepository;
import com.kosta.saladMan.service.inventory.InventoryServiceImpl;
import com.kosta.saladMan.service.notice.NoticeServiceImpl;
import com.kosta.saladMan.service.saleOrder.SalesServiceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final SalesServiceImpl salesService;
    private final InventoryServiceImpl inventoryService;
    private final NoticeServiceImpl noticeService;
    private final StoreRepository storeRepository;
    private final StoreInventoryDslRepository storeInventoryDslRepository;

    public DashboardSummaryDto getSummary(String startDate, String endDate) {
        DashboardSummaryDto result = new DashboardSummaryDto();

        // 1. 전체 매출
        SalesResultDto sales;
        try {
            sales = salesService.getTotalSales(
                LocalDate.parse(startDate),
                LocalDate.parse(endDate),
                GroupType.DAY
            );
        } catch (Exception e) {
            e.printStackTrace();
            sales = new SalesResultDto();
        }
        result.setSales(sales);

        // 2. 매장별 매출
        List<StoreSalesResultDto> stores = storeRepository.findAll().stream()
            .filter(s -> s.getId() != 1)
            .map(s -> {
                try {
                    return salesService.getStoreSales(
                        s.getId(),
                        LocalDate.parse(startDate),
                        LocalDate.parse(endDate),
                        GroupType.DAY
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                    return new StoreSalesResultDto();
                }
            })
            .collect(Collectors.toList());
        result.setStores(stores);

        // 3. 임박재고 TOP3+전체건수 (단일 DTO)
        InventoryExpireSummaryDto expireSummary = inventoryService.getExpireSummaryTop3WithCountMerged(startDate, endDate);
        result.setExpireSummary(expireSummary);

        // 4. 폐기 TOP3+전체건수 (단일 DTO)
        DisposalSummaryDto disposalSummary = inventoryService.getDisposalSummaryTop3WithCountMerged(startDate, endDate);
        result.setDisposalSummary(disposalSummary);

        // 5. 발주 TOP3+전체건수 (단일 DTO)
        OrderSummaryDto orderSummary = salesService.getOrderSummaryTop3WithCountMerged(startDate, endDate);
        result.setOrderSummary(orderSummary);

        // 6. 최근 공지 5개
        @SuppressWarnings("unchecked")
        List<NoticeDto> notices = (List<NoticeDto>) noticeService.searchNoticeList(0, 5, "title", null)
                .get("noticeList");
        result.setNotices(notices);

        // 7. 인기메뉴 Top5
        List<MenuSalesDto> topMenus = sales.getPopularMenus() != null
                ? sales.getPopularMenus().stream().limit(5).collect(Collectors.toList())
                : List.of();
        result.setTopMenus(topMenus);
        
        //8. 재고부족
        int lowStockCount = inventoryService.getLowStockCount();
        result.setLowStockCount(lowStockCount);

        return result;
    }
    
    public List<MainStockSummaryDto> getTopUsedIngredients(Integer storeId, int days, int limit) {
        LocalDate monthAgo = LocalDate.now().minusDays(days);
        return storeInventoryDslRepository.findTopUsedIngredients(storeId, monthAgo, limit);
    }

    
//    public StoreDashboardSummaryDto getStoreSummary(Integer storeId, String startDate, String endDate, int weekNo) {
//        StoreDashboardSummaryDto dto = new StoreDashboardSummaryDto();
//
//        // 1. 매출, 인기메뉴
//        SalesResultDto sales = salesService.getStoreSales(
//            storeId, LocalDate.parse(startDate), LocalDate.parse(endDate), GroupType.DAY);
//        dto.setSales(sales);
//        dto.setTopMenus(sales.getPopularMenus());
//
//        // 2. 임박/폐기
//        InventoryExpireSummaryDto expireSummary = inventoryService.getExpireSummaryTop3WithCountMerged(startDate, endDate, storeId);
//        dto.setExpireSummary(expireSummary);
//
//        // 3. 자동발주 예정 품목 수
//        int autoOrderExpectedCount = inventoryService.getAutoOrderExpectedCount(storeId);
//        dto.setAutoOrderExpectedCount(autoOrderExpectedCount);
//
//        // 4. 주요 재고 현황
//        List<MainStockSummaryDto> mainStocks = inventoryService.getTopUsedIngredients(storeId, 30, 5);
//        dto.setMainStocks(mainStocks);
//
//
//        // 5. 공지
//        List<NoticeDto> notices = noticeService.searchNoticeList(0, 5, "title", null).get("noticeList");
//        dto.setNotices(notices);
//
//        // 6. 미확인 문의
//        int unreadComplaintCount = complaintService.countUnreadComplaintsByStore(storeId);
//        dto.setUnreadComplaintCount(unreadComplaintCount);
//
//        // 7. 주간 근무표
//        List<ScheduleDto> weekSchedules = scheduleService.getWeekSchedule(storeId, weekNo);
//        dto.setWeekSchedules(weekSchedules);
//
//        return dto;
//    }

    
    
}
