package com.kosta.saladMan.service.storeInquiry;

import java.util.List;

import com.kosta.saladMan.dto.storeInquiry.FindStoreDto;

public interface StoreInquiryService {
	List<FindStoreDto> findOtherStores(Integer storeId);
}
