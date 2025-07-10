package com.kosta.saladMan.dto.dashboard;

import java.util.List;

import com.kosta.saladMan.dto.notice.ComplaintDto;
import com.kosta.saladMan.dto.notice.NoticeDto;
import com.kosta.saladMan.dto.saleOrder.SalesResultDto;
import com.kosta.saladMan.dto.saleOrder.SalesResultDto.MenuSalesDto;
import com.kosta.saladMan.dto.saleOrder.StoreSalesResultDto;

import lombok.Data;

@Data
public class DashboardSummaryDto {
    private SalesResultDto sales; // 전국매출(일/주/월별)
    private List<StoreSalesResultDto> stores; // 지점별 매출
    private List<MenuSalesDto> topMenus; // 인기메뉴 Top5+기타
    private InventoryExpireSummaryDto expireSummary; // 임박재고 TOP3+전체건수
    private DisposalSummaryDto disposalSummary; // 폐기 TOP3+전체건수
    private OrderSummaryDto orderSummary; // 발주 TOP3+전체건수
    private List<NoticeDto> notices; // 최근 공지
    private List<ComplaintDto> complaints; // 최근 불편사항
    private int lowStockCount;//재고부족
}