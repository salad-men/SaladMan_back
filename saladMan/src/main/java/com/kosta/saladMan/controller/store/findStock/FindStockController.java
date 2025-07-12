package com.kosta.saladMan.controller.store.findStock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.saladMan.dto.storeInquiry.NearbyInventoryDto;
import com.kosta.saladMan.service.findStock.FindStockService;
import com.kosta.saladMan.util.PageInfo;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
public class FindStockController {
	
	private final FindStockService findStockService;
	
	@GetMapping("/nearby-inventory")
	public ResponseEntity<List<NearbyInventoryDto>> getNearbyInventory(@RequestParam(required = false) Map<String,String> param) {
		double lat = Double.parseDouble(param.get("lat"));
		double lng = Double.parseDouble(param.get("lng"));
		double radiusKm = Double.parseDouble(param.get("radiusKm"));

	    try {
	    	if (param.get("lat") != null) lat = Double.parseDouble(param.get("lat"));
		    if (param.get("lng") != null) lng = Double.parseDouble(param.get("lng"));
		    if (param.get("radiusKm") != null) radiusKm = Double.parseDouble(param.get("radiusKm"));

	        List<NearbyInventoryDto> allResults = findStockService.findNearbyInventory(lat, lng, radiusKm);
	        return ResponseEntity.ok(allResults);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }
	}
}
