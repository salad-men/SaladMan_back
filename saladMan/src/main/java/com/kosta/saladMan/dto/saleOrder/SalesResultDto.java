package com.kosta.saladMan.dto.saleOrder;

import java.util.List;

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
public class SalesResultDto {
	private SummaryDto summary;
    private List<DailySalesDto> daily;
    private List<DailySalesDto> weekly;   
    private List<DailySalesDto> monthly;
    private List<MenuSalesDto> popularMenus;
    	
	@Getter
    @Setter
    public static class SummaryDto {
        private String period;
        private int totalQuantity;
        private int totalRevenue;
    }
    
    @Getter
    @Setter
    public static class DailySalesDto {
        private String date;
        private Integer quantity;
        private Integer revenue;
        private Integer cost;
        private Integer profit;
    }
    
    @Getter
    @Setter
    public static class MenuSalesDto {
        private String menuName;
        private int quantity;
    }
    
    public enum GroupType {
        DAY, WEEK, MONTH
    }

}
