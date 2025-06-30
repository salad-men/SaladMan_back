package com.kosta.saladMan.dto.saleOrder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kosta.saladMan.dto.saleOrder.SalesResultDto.DailySalesDto;
import com.kosta.saladMan.dto.saleOrder.SalesResultDto.MenuSalesDto;
import com.kosta.saladMan.dto.saleOrder.SalesResultDto.SummaryDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreSalesResultDto {
	private Integer storeId;
    private SummaryDto summary;
    private List<DailySalesDto> daily;
    private List<MenuSalesDto> popularMenus;
    
}

