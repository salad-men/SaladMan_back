package com.kosta.saladMan.service.saleOrder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.kosta.saladMan.dto.menu.TotalMenuDto;
import com.kosta.saladMan.dto.saleOrder.PaymentListDto;
import com.kosta.saladMan.dto.saleOrder.SalesResultDto;
import com.kosta.saladMan.dto.saleOrder.SalesResultDto.GroupType;
import com.kosta.saladMan.entity.menu.TotalMenu;
import com.kosta.saladMan.dto.saleOrder.StoreSalesResultDto;
import com.kosta.saladMan.repository.saleOrder.SalesDslRepository;
import com.kosta.saladMan.util.PageInfo;

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

	@Override
	public List<PaymentListDto> getPaymentList(Integer storeId, String status, String start, String end,
			PageInfo pageInfo) throws Exception {
		
		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 10);
		Page<PaymentListDto> pages = salesDslRepository.getPaymentList(storeId, status, start, end, pageRequest);
		
		pageInfo.setAllPage(pages.getTotalPages());
		int startPage = (pageInfo.getAllPage() - 1) / 10 * 10 + 1;
		int endPage = Math.min(startPage + 9, pageInfo.getAllPage());
		pageInfo.setStartPage(startPage);
		pageInfo.setEndPage(endPage);
		
		 return pages.getContent();
	}
}
