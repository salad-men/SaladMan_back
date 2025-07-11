package com.kosta.saladMan.controller.hq.saleOrder;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kosta.saladMan.dto.saleOrder.SalesResultDto;
import com.kosta.saladMan.dto.saleOrder.SalesResultDto.GroupType;
import com.kosta.saladMan.dto.saleOrder.StoreFilterDto;
import com.kosta.saladMan.dto.saleOrder.StoreSalesResultDto;
import com.kosta.saladMan.repository.StoreRepository;
import com.kosta.saladMan.service.saleOrder.SalesService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/hq")
@RequiredArgsConstructor
public class HqSalesController {
	
	private final SalesService salesService;
	
	//total매출조회
    @GetMapping("/totalSales")
    public ResponseEntity<SalesResultDto> getHqSales(@RequestParam String startDate, @RequestParam String endDate,
    		@RequestParam(defaultValue = "DAY") String groupType) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        GroupType type = GroupType.valueOf(groupType.toUpperCase());
        
        try {
        	SalesResultDto response = salesService.getTotalSales(start, end, type);
        	return ResponseEntity.ok(response);
        } catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
    }
	
	//store 매출조회
    @GetMapping("/storeSales")
    public ResponseEntity<StoreSalesResultDto> getHqSales(@RequestParam Integer storeId,
    		@RequestParam String startDate, @RequestParam String endDate, @RequestParam(defaultValue = "DAY") String groupType) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        GroupType type = GroupType.valueOf(groupType.toUpperCase());
        
        try {
        	StoreSalesResultDto response = salesService.getStoreSales(storeId,start, end, type);
        	return ResponseEntity.ok(response);
        } catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
    }
    
    @GetMapping("/storeSales/filter")
    public ResponseEntity<List<StoreFilterDto>> getStoreFilterOptions() {
    	try {
    		return new ResponseEntity<>(salesService.getStoreFilter(),HttpStatus.OK);
    	}catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    	
    	}
    }
}
