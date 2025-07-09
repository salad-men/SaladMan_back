package com.kosta.saladMan.service.store;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kosta.saladMan.dto.store.CloseStoreDto;
import com.kosta.saladMan.dto.store.ResetStorePasswordDto;
import com.kosta.saladMan.dto.store.StoreDto;
import com.kosta.saladMan.dto.store.StoreUpdateDto;
import com.kosta.saladMan.entity.store.Store;

public interface StoreManagementService {
	
	Integer storeRegister(StoreDto storeDto) throws Exception;
	Boolean isStoreNameDouble(String storeName) throws Exception;
	Boolean isStoreUsernameDouble(String storeUsername) throws Exception;
    Page<StoreDto> searchStores(String region, String status, String keyword, Pageable pageable);
    StoreDto getStoreDetail(Integer id) throws Exception;
	Boolean updateStore(StoreUpdateDto storeUpdateDto) throws Exception;
	void resetStorePassword(Store admin, ResetStorePasswordDto dto) throws Exception;
	void closeStore(CloseStoreDto dto) throws Exception;

}
