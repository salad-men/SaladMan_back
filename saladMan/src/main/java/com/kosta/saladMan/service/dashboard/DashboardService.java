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
import com.kosta.saladMan.dto.store.ScheduleDto;
import com.kosta.saladMan.dto.notice.NoticeDto;
import com.kosta.saladMan.dto.saleOrder.SalesResultDto.GroupType;
import com.kosta.saladMan.dto.saleOrder.SalesResultDto.MenuSalesDto;
import com.kosta.saladMan.repository.StoreRepository;
import com.kosta.saladMan.repository.empolyee.EmployeeDslRepository;
import com.kosta.saladMan.repository.inventory.StoreInventoryDslRepository;
import com.kosta.saladMan.repository.notice.ComplaintRepository;
import com.kosta.saladMan.service.empolyee.EmployeeServiceImpl;
import com.kosta.saladMan.service.inventory.InventoryServiceImpl;
import com.kosta.saladMan.service.notice.ComplaintServiceImpl;
import com.kosta.saladMan.service.notice.NoticeServiceImpl;
import com.kosta.saladMan.service.order.OrderServiceImpl;
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
    private final ComplaintServiceImpl complaintService;
    private final ComplaintRepository complaintRepository;
    private final OrderServiceImpl orderService;
    private final EmployeeDslRepository employeeDslRepository;

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

        // 3. 임박재고 TOP3+전체건수
        InventoryExpireSummaryDto expireSummary = inventoryService.getExpireSummaryTop3WithCountMerged(startDate, endDate);
        result.setExpireSummary(expireSummary);

        // 4. 폐기 TOP3+전체건수
        DisposalSummaryDto disposalSummary = inventoryService.getDisposalSummaryTop3WithCountMerged(startDate, endDate);
        result.setDisposalSummary(disposalSummary);

        // 5. 발주 TOP3+전체건수
        OrderSummaryDto orderSummary = salesService.getOrderSummaryTop3WithCountMerged(startDate, endDate);
        result.setOrderSummary(orderSummary);

        // 6. 최근 공지 5개
        List<NoticeDto> notices = (List<NoticeDto>) noticeService.searchNoticeList(0, 5, "title", null)
                .get("noticeList");
        result.setNotices(notices);

        // 7. 인기메뉴 Top5
        List<MenuSalesDto> topMenus = sales.getPopularMenus() != null
                ? sales.getPopularMenus().stream().limit(5).collect(Collectors.toList())
                : List.of();
        result.setTopMenus(topMenus);
        
        // 8. 재고부족
        int lowStockCount = inventoryService.getLowStockCount();
        result.setLowStockCount(lowStockCount);

        return result;
    }

    // 주간 근무표 조회 (year는 내부에서 자동으로 처리)
    public List<ScheduleDto> getWeekSchedule(Integer storeId, Integer weekNo) {
        int year = LocalDate.now().getYear();  // 현재 연도를 자동으로 가져옵니다.
        
        // employeeDslRepository에서 직접 데이터를 가져옵니다.
        return employeeDslRepository.findWeekSchedulesByStore(storeId, weekNo);  
    }

    // 매장 대시보드 요약 전체 반환
    public StoreDashboardSummaryDto getStoreSummary(Integer storeId, String startDate, String endDate, int weekNo) {
        StoreDashboardSummaryDto dto = new StoreDashboardSummaryDto();

        // 매출/주문/인기메뉴
        StoreSalesResultDto sales = salesService.getStoreSales(storeId, LocalDate.parse(startDate), LocalDate.parse(endDate), SalesResultDto.GroupType.DAY);
        dto.setSales(sales);
        dto.setTopMenus(sales.getPopularMenus());

        // 임박/폐기 예정 재고 요약
        InventoryExpireSummaryDto expireSummary = inventoryService.getStoreExpireSummary(storeId, startDate, endDate);
        dto.setExpireSummary(expireSummary);

        // 자동 발주 예정 품목 수
        int autoOrderExpectedCount = orderService.getAutoOrderExpectedCount(storeId);
        dto.setAutoOrderExpectedCount(autoOrderExpectedCount);

        // 주요 재고
        List<MainStockSummaryDto> mainStocks = inventoryService.getMainStocksByMonth(storeId);
        dto.setMainStocks(mainStocks);

        // 최근 공지사항 (최신 5개)
        List<NoticeDto> notices = noticeService.getRecentNotices(5);
        dto.setNotices(notices);

        // 고객문의 수
        int unreadComplaintCount = complaintService.countUnreadComplaintsByStore(storeId);
        dto.setUnreadComplaintCount(unreadComplaintCount);

        // 주간 근무표
        List<ScheduleDto> weekSchedules = getWeekSchedule(storeId, weekNo);  
        dto.setWeekSchedules(weekSchedules);

        return dto;
    }
}

