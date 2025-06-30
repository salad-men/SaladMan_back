package com.kosta.saladMan.service.storeInquiry;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.kosta.saladMan.dto.storeInquiry.FindStoreDto;
import com.kosta.saladMan.entity.store.Store;
import com.kosta.saladMan.repository.storeInquiry.StoreInquiryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreInquiryServiceImpl implements StoreInquiryService{
	
	private final StoreInquiryRepository storeInquiryRepository;
	
	@Override
	public List<FindStoreDto> findOtherStores(Integer storeId) {
		List<Store> store = storeInquiryRepository.findOtherStores(storeId);
		return store.stream()
				.map(s -> new FindStoreDto(s.getId(), s.getName(),s.getLocation(),s.getLatitude(),s.getLongitude()))
				.collect(Collectors.toList());
	}

}
