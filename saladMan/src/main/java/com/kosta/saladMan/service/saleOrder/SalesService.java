package com.kosta.saladMan.service.saleOrder;

import java.time.LocalDate;
import java.util.List;

import com.kosta.saladMan.dto.dashboard.OrderSummaryDto;
import com.kosta.saladMan.dto.saleOrder.PaymentListDto;
import com.kosta.saladMan.dto.saleOrder.SalesResultDto;
import com.kosta.saladMan.dto.saleOrder.SalesResultDto.GroupType;
import com.kosta.saladMan.dto.saleOrder.StoreFilterDto;
import com.kosta.saladMan.util.PageInfo;
import com.kosta.saladMan.dto.saleOrder.StoreSalesResultDto;

public interface SalesService {
	StoreSalesResultDto getStoreSales(Integer storeId, LocalDate start, LocalDate end, GroupType groupType) throws Exception;
	SalesResultDto getTotalSales(LocalDate start, LocalDate end, GroupType groupType) throws Exception;
	List<PaymentListDto> getPaymentList(Integer storeId, String status, String start, String end, PageInfo pageInfo) throws Exception;
	List<StoreFilterDto> getStoreFilter() throws Exception;
	
	OrderSummaryDto getOrderSummaryTop3WithCountMerged(String startDate, String endDate);

}
