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

    public DashboardSummaryDto getSummary(String startDate, String endDate, String groupType) {
        DashboardSummaryDto result = new DashboardSummaryDto();

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        // 1. 전체(전국) 매출 일/주/월별 각각 조회
        SalesResultDto dailySales = null;
        SalesResultDto weeklySales = null;
        SalesResultDto monthlySales = null;
        try {
            dailySales = salesService.getTotalSales(start, end, SalesResultDto.GroupType.DAY);
            weeklySales = salesService.getTotalSales(start, end, SalesResultDto.GroupType.WEEK);
            monthlySales = salesService.getTotalSales(start, end, SalesResultDto.GroupType.MONTH);
        } catch (Exception e) {
            e.printStackTrace();
            // 예외 발생 시 빈 객체 반환 또는 예외 처리 로직 추가
            dailySales = new SalesResultDto();
            weeklySales = new SalesResultDto();
            monthlySales = new SalesResultDto();
        }


        // 2. 하나의 DTO로 합치기 (daily/weekly/monthly)
        SalesResultDto sales = new SalesResultDto();
        sales.setSummary(dailySales.getSummary());
        sales.setDaily(dailySales.getDaily());
        sales.setWeekly(weeklySales.getDaily());
        sales.setMonthly(monthlySales.getDaily());
        sales.setPopularMenus(dailySales.getPopularMenus());
        result.setSales(sales);

        // 3. 매장별 매출 (StoreSalesResultDto) - 일/주/월별 모두 포함
        List<StoreSalesResultDto> stores = storeRepository.findAll().stream()
            .filter(s -> s.getId() != 1) // 본사 제외
            .map(s -> {
                try {
                    StoreSalesResultDto daily = salesService.getStoreSales(
                            s.getId(), start, end, SalesResultDto.GroupType.DAY);
                    StoreSalesResultDto weekly = salesService.getStoreSales(
                            s.getId(), start, end, SalesResultDto.GroupType.WEEK);
                    StoreSalesResultDto monthly = salesService.getStoreSales(
                            s.getId(), start, end, SalesResultDto.GroupType.MONTH);

                    StoreSalesResultDto storeResult = new StoreSalesResultDto();
                    storeResult.setStoreId(s.getId());
                    storeResult.setSummary(daily.getSummary());
                    storeResult.setDaily(daily.getDaily());
                    storeResult.setWeekly(weekly.getDaily());
                    storeResult.setMonthly(monthly.getDaily());
                    storeResult.setPopularMenus(daily.getPopularMenus());
                    return storeResult;
                } catch (Exception e) {
                    e.printStackTrace();
                    return new StoreSalesResultDto();
                }
            })
            .collect(Collectors.toList());
        result.setStores(stores);

        // 4. 임박재고 TOP3+전체건수
        InventoryExpireSummaryDto expireSummary = inventoryService.getExpireSummaryTop3WithCountMerged(startDate, endDate);
        result.setExpireSummary(expireSummary);

        // 5. 폐기 TOP3+전체건수
        DisposalSummaryDto disposalSummary = inventoryService.getDisposalSummaryTop3WithCountMerged(startDate, endDate);
        result.setDisposalSummary(disposalSummary);

        // 6. 발주 TOP3+전체건수
        OrderSummaryDto orderSummary = salesService.getOrderSummaryTop3WithCountMerged(startDate, endDate);
        result.setOrderSummary(orderSummary);

        // 7. 최근 공지 5개
        List<NoticeDto> notices = (List<NoticeDto>) noticeService.searchNoticeList(0, 5, "title", null).get("noticeList");
        result.setNotices(notices);

        // 8. 인기메뉴 Top5 (전국매출 기준)
        List<MenuSalesDto> topMenus = sales.getPopularMenus() != null
                ? sales.getPopularMenus().stream().limit(5).collect(Collectors.toList())
                : List.of();
        result.setTopMenus(topMenus);

        // 9. 재고부족
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
    public StoreDashboardSummaryDto getStoreSummary(Integer storeId, String startDate, String endDate, String groupType, int weekNo) {
        StoreDashboardSummaryDto dto = new StoreDashboardSummaryDto();

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        // 1. 매출/주문 (일/주/월별 전체 반환)
        StoreSalesResultDto dailySales = salesService.getStoreSales(storeId, start, end, GroupType.DAY);
        StoreSalesResultDto weeklySales = salesService.getStoreSales(storeId, start, end, GroupType.WEEK);
        StoreSalesResultDto monthlySales = salesService.getStoreSales(storeId, start, end, GroupType.MONTH);

        // StoreSalesResultDto에 일/주/월별 리스트를 모두 담아서 전달 (프론트에서 groupType별로 스위칭)
        StoreSalesResultDto sales = new StoreSalesResultDto();
        sales.setSummary(dailySales.getSummary());
        sales.setDaily(dailySales.getDaily());
        sales.setWeekly(weeklySales.getDaily());
        sales.setMonthly(monthlySales.getDaily());
        // 인기메뉴는 무조건 DAY 기준(기간 필터)
        sales.setPopularMenus(dailySales.getPopularMenus());

        dto.setSales(sales);
        dto.setTopMenus(
            dailySales.getPopularMenus() != null
            ? dailySales.getPopularMenus().stream().limit(5).collect(Collectors.toList())
            : List.of()
        );

        // 2. 임박/폐기 예정 재고 요약
        InventoryExpireSummaryDto expireSummary = inventoryService.getStoreExpireSummary(storeId, startDate, endDate);
        dto.setExpireSummary(expireSummary);

        // 3. 자동 발주 예정 품목 수
        int autoOrderExpectedCount = orderService.getAutoOrderExpectedCount(storeId);
        dto.setAutoOrderExpectedCount(autoOrderExpectedCount);

        // 4. 주요 재고
        List<MainStockSummaryDto> mainStocks = inventoryService.getMainStocksByMonth(storeId);
        dto.setMainStocks(mainStocks);

        // 5. 최근 공지사항 (최신 5개)
        List<NoticeDto> notices = noticeService.getRecentNotices(5);
        dto.setNotices(notices);

        // 6. 고객문의 수
        int unreadComplaintCount = complaintService.countUnreadComplaintsByStore(storeId);
        dto.setUnreadComplaintCount(unreadComplaintCount);

        // 7. 주간 근무표
        List<ScheduleDto> weekSchedules = getWeekSchedule(storeId, weekNo);
        dto.setWeekSchedules(weekSchedules);

        return dto;
    }
}

