package com.kosta.saladMan.controller.store.saleOrder;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kosta.saladMan.dto.saleOrder.StoreSalesViewDto;
import com.kosta.saladMan.dto.saleOrder.StoreSalesViewDto.GroupType;
import com.kosta.saladMan.service.saleOrder.SalesService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/store")
@RequiredArgsConstructor
public class SalesController {
	
	private final SalesService salesService;
	
	//store 매출조회
    @GetMapping("/storeSales")
    public ResponseEntity<StoreSalesViewDto> getHqSales(
    		@RequestParam String startDate, @RequestParam String endDate, @RequestParam(defaultValue = "DAY") String groupType) {
        
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        GroupType type = GroupType.valueOf(groupType.toUpperCase());
        
        try {
        	StoreSalesViewDto response = salesService.getHqSales(start, end, type);
        	return ResponseEntity.ok(response);
        } catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
    }

}
