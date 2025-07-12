package com.kosta.saladMan.dto.dashboard;


import java.util.List;

import com.kosta.saladMan.dto.notice.NoticeDto;
import com.kosta.saladMan.dto.saleOrder.SalesResultDto;
import com.kosta.saladMan.dto.saleOrder.SalesResultDto.MenuSalesDto;
import com.kosta.saladMan.dto.saleOrder.StoreSalesResultDto;
import com.kosta.saladMan.dto.store.ScheduleDto;

import lombok.Data;

@Data
public class StoreDashboardSummaryDto {
    private StoreSalesResultDto sales;                // 매장 매출(일/주/월별)
    private List<MenuSalesDto> topMenus;              // 인기메뉴 TOP5
    private InventoryExpireSummaryDto expireSummary;  // 임박/폐기예정 재고
    private int autoOrderExpectedCount;               // 자동발주 예정 품목 수
    private List<MainStockSummaryDto> mainStocks;     // 한달간 사용량 높은 주요 재고
    private int lowStockCount;                        // 재고부족 품목수
    private List<NoticeDto> notices;                  // 최근 공지사항(최신 5개)
    private int unreadComplaintCount;                 // 미확인 고객문의 수
    private List<ScheduleDto> weekSchedules;          // 주간 근무표
    
}