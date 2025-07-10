package com.kosta.saladMan.controller.hq.dashboard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.saladMan.dto.dashboard.StoreDashboardSummaryDto;
import com.kosta.saladMan.service.dashboard.DashboardService;
import com.kosta.saladMan.service.inventory.InventoryService;
import com.kosta.saladMan.service.saleOrder.SalesService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/store/dashboard")
@RequiredArgsConstructor
public class StoreDashboardController {

    private final DashboardService dashboardService;

//	@GetMapping("/summary")
//	public ResponseEntity<?> getStoreSummary(
//	        @RequestParam Integer storeId,
//	        @RequestParam String startDate,
//	        @RequestParam String endDate,
//	        @RequestParam(required = false, defaultValue = "1") int weekNo) {
//	    StoreDashboardSummaryDto result = dashboardService.getStoreSummary(storeId, startDate, endDate, weekNo);
//	    return ResponseEntity.ok(result);
//	}

}
