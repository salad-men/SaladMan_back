package com.kosta.saladMan.dto.saleOrder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

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

public class StoreSalesViewDto {
    private SummaryDto summary;
    private List<DailySalesDto> daily;
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
        private int quantity;
        private int revenue;
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

