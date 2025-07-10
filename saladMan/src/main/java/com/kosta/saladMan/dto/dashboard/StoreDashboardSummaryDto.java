package com.kosta.saladMan.dto.dashboard;


import java.util.List;

import com.kosta.saladMan.dto.notice.NoticeDto;
import com.kosta.saladMan.dto.saleOrder.SalesResultDto;
import com.kosta.saladMan.dto.saleOrder.SalesResultDto.MenuSalesDto;
import com.kosta.saladMan.dto.store.ScheduleDto;

import lombok.Data;

@Data
public class StoreDashboardSummaryDto {
    private SalesResultDto sales;                     // 매장별 매출(일/주/월)
    private List<MenuSalesDto> topMenus;              // 인기메뉴 TOP5
    private InventoryExpireSummaryDto expireSummary;  // 임박/폐기예정 재고 요약
    private int autoOrderExpectedCount;               // 자동발주 예정 품목 수
    private List<MainStockSummaryDto> mainStocks;     // 한달간 사용량 높은 주요 재고 품목
    private List<NoticeDto> notices;                  // 최근 공지사항
    private int unreadComplaintCount;                 // 미확인 고객문의 수
    private List<ScheduleDto> weekSchedules;          // 해당주 근무표 (employeeId+date+shiftType)
}