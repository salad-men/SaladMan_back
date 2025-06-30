package com.kosta.saladMan.service.saleOrder;

import java.time.LocalDate;

import com.kosta.saladMan.dto.saleOrder.SalesResultDto;
import com.kosta.saladMan.dto.saleOrder.SalesResultDto.GroupType;
import com.kosta.saladMan.dto.saleOrder.StoreSalesResultDto;

public interface SalesService {
	StoreSalesResultDto getStoreSales(Integer storeId, LocalDate start, LocalDate end, GroupType groupType) throws Exception;
	SalesResultDto getTotalSales(LocalDate start, LocalDate end, GroupType groupType) throws Exception;
}
