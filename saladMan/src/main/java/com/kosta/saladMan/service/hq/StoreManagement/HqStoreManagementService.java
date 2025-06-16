package com.kosta.saladMan.service.hq.StoreManagement;

import com.kosta.saladMan.dto.store.StoreDto;

public interface HqStoreManagementService {
	
	void storeRegister(StoreDto storeDto) throws Exception;
	Boolean isStoreNameDouble(String storeName) throws Exception;
	Boolean isStoreUsernameDouble(String storeUsername) throws Exception;
	
}
