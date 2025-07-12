package com.kosta.saladMan.service.findStock;

import java.util.List;

import com.kosta.saladMan.dto.storeInquiry.NearbyInventoryDto;
import com.kosta.saladMan.dto.storeInquiry.StoreInventoryResponseDto;

public interface FindStockService {
	List<NearbyInventoryDto> findNearbyInventory(double lat, double lng, double radiusKm) throws Exception;
	double getDistance(double lat1, double lon1, double lat2, double lon2);
}
