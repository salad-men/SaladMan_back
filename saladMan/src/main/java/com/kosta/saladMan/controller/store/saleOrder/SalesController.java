package com.kosta.saladMan.controller.store.saleOrder;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kosta.saladMan.auth.PrincipalDetails;
import com.kosta.saladMan.dto.saleOrder.PaymentListDto;
import com.kosta.saladMan.dto.saleOrder.StoreSalesResultDto;
import com.kosta.saladMan.dto.saleOrder.SalesResultDto.GroupType;
import com.kosta.saladMan.service.saleOrder.SalesService;
import com.kosta.saladMan.util.PageInfo;

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

    @GetMapping("/paymentList")
    public ResponseEntity<Map<String, Object>> getPaymentList(@AuthenticationPrincipal PrincipalDetails principalDetails,
    		@RequestParam(required = false) String status,@RequestParam(required = false) String startDate, 
    		@RequestParam(required = false) String endDate,@RequestParam Integer page) {
    	Integer storeId = principalDetails.getStore().getId();
    	
    	// 빈 문자열이면 null로 변환
        status = (status != null && !status.isBlank()) ? status : null;
        startDate = (startDate != null && !startDate.isBlank()) ? startDate : null;
        endDate = (endDate != null && !endDate.isBlank()) ? endDate : null;
        
        PageInfo pageInfo = new PageInfo(page);
    	
        try {
        	List<PaymentListDto> list = salesService.getPaymentList(storeId, status, startDate, endDate, pageInfo);
        	Map<String, Object> response = new HashMap<>();
        	response.put("content", list);
        	response.put("pageInfo", pageInfo);
        	return ResponseEntity.ok(response);
        }catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
    }
}
