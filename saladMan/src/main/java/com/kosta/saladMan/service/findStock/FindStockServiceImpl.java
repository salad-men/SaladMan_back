package com.kosta.saladMan.service.findStock;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.kosta.saladMan.dto.storeInquiry.NearbyInventoryDto;
import com.kosta.saladMan.dto.storeInquiry.StoreInventoryResponseDto;
import com.kosta.saladMan.entity.store.Store;
import com.kosta.saladMan.repository.StoreRepository;
import com.kosta.saladMan.repository.findStock.FindStockDslRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FindStockServiceImpl implements FindStockService{

	private final FindStockDslRepository storeInquiryDslRepository;
	private final StoreRepository storeRepository;
	
	@Override
	public List<NearbyInventoryDto> findNearbyInventory(double lat, double lng, double radiusKm) throws Exception {
		 // 1. 전체 매장 목록 가져오기
        List<Store> allStores = storeRepository.findAll();

        // 2. 거리 계산 후 반경 내 매장 ID 추출
        List<Integer> nearbyStoreIds = allStores.stream()
        	    .filter(store -> getDistance(lat, lng, store.getLatitude(), store.getLongitude()) <= radiusKm)
        	    .map(Store::getId)
        	    .collect(Collectors.toList());

        // 3. QueryDSL로 재고 조회
        return storeInquiryDslRepository.findInventoryByStoreIds(nearbyStoreIds);
	}
	
	@Override
	public double getDistance(double lat1, double lon1, double lat2, double lon2) {
		double R = 6371; // km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
	}

}
