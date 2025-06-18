package com.kosta.saladMan.service.hq;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kosta.saladMan.dto.store.StoreDto;

public interface HqStoreManagementService {
	
	void storeRegister(StoreDto storeDto) throws Exception;
	Boolean isStoreNameDouble(String storeName) throws Exception;
	Boolean isStoreUsernameDouble(String storeUsername) throws Exception;
    Page<StoreDto> searchStores(String region, String status, String keyword, Pageable pageable);
    StoreDto getStoreDetail(Integer id) throws Exception;
	Boolean updateStore(StoreDto storeDto) throws Exception;
}
