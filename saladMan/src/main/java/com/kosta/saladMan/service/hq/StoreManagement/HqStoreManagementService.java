package com.kosta.saladMan.service.hq.StoreManagement;

import com.kosta.saladMan.dto.store.StoreDto;

public interface HqStoreManagementService {
	
	void storeRegister(StoreDto storeDto) throws Exception;
}
