package com.kosta.saladMan.service.saleOrder;

import java.time.LocalDate;

import com.kosta.saladMan.dto.saleOrder.StoreSalesViewDto;
import com.kosta.saladMan.dto.saleOrder.StoreSalesViewDto.GroupType;

public interface SalesService {
	StoreSalesViewDto getHqSales(LocalDate start, LocalDate end, GroupType groupType) throws Exception;
}
