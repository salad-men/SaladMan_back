package com.kosta.saladMan.service.saleOrder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kosta.saladMan.dto.saleOrder.StoreSalesViewDto;
import com.kosta.saladMan.dto.saleOrder.StoreSalesViewDto.GroupType;
import com.kosta.saladMan.repository.saleOrder.SalesDslRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SalesServiceImpl implements SalesService {

    private final SalesDslRepository salesDslRepository;

    @Override
    public StoreSalesViewDto getStoreSales(Integer storeId, LocalDate start, LocalDate end, GroupType groupType) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);
        System.out.println("startDate: " + start);
        System.out.println("endDate: " + end);

        // 1. 일별 판매량/매출
        List<StoreSalesViewDto.DailySalesDto> daily = salesDslRepository.getGroupedSales(storeId, startDateTime, endDateTime, groupType);
        // 2. 메뉴별 판매량
        List<StoreSalesViewDto.MenuSalesDto> popular = salesDslRepository.getMenuSales(storeId, startDateTime, endDateTime);
        // 3. 요약 정보
        int totalQuantity = daily.stream().mapToInt(StoreSalesViewDto.DailySalesDto::getQuantity).sum();
        int totalRevenue = daily.stream().mapToInt(StoreSalesViewDto.DailySalesDto::getRevenue).sum();

        var summary = new StoreSalesViewDto.SummaryDto();
        summary.setPeriod(start + " ~ " + end);
        summary.setTotalQuantity(totalQuantity);
        summary.setTotalRevenue(totalRevenue);

        var response = new StoreSalesViewDto();
        response.setStoreId(storeId);
        response.setSummary(summary);
        response.setDaily(daily);
        response.setPopularMenus(popular);

        return response;
    }
}
