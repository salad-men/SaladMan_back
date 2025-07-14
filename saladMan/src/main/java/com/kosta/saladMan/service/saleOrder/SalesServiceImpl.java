package com.kosta.saladMan.service.saleOrder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.kosta.saladMan.dto.dashboard.OrderSummaryDto;
import com.kosta.saladMan.dto.menu.TotalMenuDto;
import com.kosta.saladMan.dto.saleOrder.PaymentListDto;
import com.kosta.saladMan.dto.saleOrder.SalesResultDto;
import com.kosta.saladMan.dto.saleOrder.SalesResultDto.GroupType;
import com.kosta.saladMan.dto.saleOrder.StoreFilterDto;
import com.kosta.saladMan.entity.menu.TotalMenu;
import com.kosta.saladMan.dto.saleOrder.StoreSalesResultDto;
import com.kosta.saladMan.repository.StoreRepository;
import com.kosta.saladMan.repository.saleOrder.SalesDslRepository;
import com.kosta.saladMan.util.PageInfo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SalesServiceImpl implements SalesService {

    private final SalesDslRepository salesDslRepository;
    private final StoreRepository storeRepository;

    @Override
    public StoreSalesResultDto getStoreSales(
            Integer storeId,
            LocalDate start,
            LocalDate end,
            GroupType groupType) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);

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
        // 1. 기간 범위 설정
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);

        // 2. 일/주/월 그룹별로, dslRepository에서 데이터 조회
        List<SalesResultDto.DailySalesDto> salesList = salesDslRepository.getTotalSales(startDateTime, endDateTime, groupType);
        List<SalesResultDto.MenuSalesDto> popularMenus = salesDslRepository.getTotalMenuSales(startDateTime, endDateTime);

        // 3. 집계
        int totalQuantity = salesList.stream()
                .mapToInt(d -> d.getQuantity() == null ? 0 : d.getQuantity())
                .sum();
        int totalRevenue = salesList.stream()
                .mapToInt(d -> d.getRevenue() == null ? 0 : d.getRevenue())
                .sum();

        SalesResultDto.SummaryDto summary = new SalesResultDto.SummaryDto();
        summary.setPeriod(start + " ~ " + end);
        summary.setTotalQuantity(totalQuantity);
        summary.setTotalRevenue(totalRevenue);

        // 4. DTO 구성
        SalesResultDto result = new SalesResultDto();
        result.setSummary(summary);
        result.setDaily(salesList); // groupType이 DAY면 일별, WEEK면 주별, MONTH면 월별
        result.setPopularMenus(popularMenus);

        return result;
    }


	@Override
	public List<PaymentListDto> getPaymentList(Integer storeId, String status, String start, String end,
			PageInfo pageInfo) throws Exception {
		
		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 10);
		Page<PaymentListDto> pages = salesDslRepository.getPaymentList(storeId, status, start, end, pageRequest);
		
		pageInfo.setAllPage(pages.getTotalPages());
		
		int startPage = ((pageInfo.getCurPage() - 1) / 5) * 5 + 1;
		int endPage = Math.min(startPage + 4, pageInfo.getAllPage());
		pageInfo.setStartPage(startPage);
		pageInfo.setEndPage(endPage);
		
		 return pages.getContent();
	}

	@Override
	public List<StoreFilterDto> getStoreFilter() throws Exception {
		return storeRepository.findAll().stream()
                .map(s -> new StoreFilterDto(s.getId(), s.getName(), s.getLocation()))
                .collect(Collectors.toList());
	}
	
	
	@Override
	public OrderSummaryDto getOrderSummaryTop3WithCountMerged(String startDate, String endDate) {
	    return salesDslRepository.findOrderSummaryTop3WithCountMerged(startDate, endDate);
	}

	
}
