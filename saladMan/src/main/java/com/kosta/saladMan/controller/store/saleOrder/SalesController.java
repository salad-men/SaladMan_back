package com.kosta.saladMan.controller.store.saleOrder;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kosta.saladMan.auth.PrincipalDetails;
import com.kosta.saladMan.dto.saleOrder.StoreSalesResultDto;
import com.kosta.saladMan.dto.saleOrder.SalesResultDto.GroupType;
import com.kosta.saladMan.service.saleOrder.SalesService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/store")
@RequiredArgsConstructor
public class SalesController {
	
	private final SalesService salesService;
	
	//store 매출조회
    @GetMapping("/storeSales")
    public ResponseEntity<StoreSalesResultDto> getHqSales(@AuthenticationPrincipal PrincipalDetails principalDetails,
    		@RequestParam String startDate, @RequestParam String endDate, @RequestParam(defaultValue = "DAY") String groupType) {
    	Integer storeId = principalDetails.getStore().getId();
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

}
