package com.kosta.saladMan.service.saleOrder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kosta.saladMan.dto.saleOrder.SalesResultDto;
import com.kosta.saladMan.dto.saleOrder.SalesResultDto.GroupType;
import com.kosta.saladMan.dto.saleOrder.StoreSalesResultDto;
import com.kosta.saladMan.repository.saleOrder.SalesDslRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SalesServiceImpl implements SalesService {

    private final SalesDslRepository salesDslRepository;

    @Override
    public StoreSalesResultDto getStoreSales(Integer storeId, LocalDate start, LocalDate end, GroupType groupType) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);
        System.out.println("startDate: " + start);
        System.out.println("endDate: " + end);

        List<SalesResultDto.DailySalesDto> daily = salesDslRepository.getStoreSales(storeId, startDateTime, endDateTime, groupType);
        List<SalesResultDto.MenuSalesDto> popular = salesDslRepository.getStoreMenuSales(storeId, startDateTime, endDateTime);

        int totalQuantity = daily.stream().mapToInt(SalesResultDto.DailySalesDto::getQuantity).sum();
        int totalRevenue = daily.stream().mapToInt(SalesResultDto.DailySalesDto::getRevenue).sum();

        var summary = new SalesResultDto.SummaryDto();
        summary.setPeriod(start + " ~ " + end);
        summary.setTotalQuantity(totalQuantity);
        summary.setTotalRevenue(totalRevenue);

        var response = new StoreSalesResultDto();
        response.setStoreId(storeId);
        response.setSummary(summary);
        response.setDaily(daily);
        response.setPopularMenus(popular);

        return response;
    }

	@Override
	public SalesResultDto getTotalSales(LocalDate start, LocalDate end, GroupType groupType) throws Exception {
		LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);
        System.out.println("startDate: " + start);
        System.out.println("endDate: " + end);

        List<SalesResultDto.DailySalesDto> daily = salesDslRepository.getTotalSales(startDateTime, endDateTime, groupType);
        List<SalesResultDto.MenuSalesDto> popular = salesDslRepository.getTotalMenuSales(startDateTime, endDateTime);

        int totalQuantity = daily.stream().mapToInt(SalesResultDto.DailySalesDto::getQuantity).sum();
        int totalRevenue = daily.stream().mapToInt(SalesResultDto.DailySalesDto::getRevenue).sum();

        var summary = new SalesResultDto.SummaryDto();
        summary.setPeriod(start + " ~ " + end);
        summary.setTotalQuantity(totalQuantity);
        summary.setTotalRevenue(totalRevenue);

        var response = new SalesResultDto();
        response.setSummary(summary);
        response.setDaily(daily);
        response.setPopularMenus(popular);

        return response;
	}
}
