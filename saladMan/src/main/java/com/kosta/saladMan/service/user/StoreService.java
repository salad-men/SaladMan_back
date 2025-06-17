package com.kosta.saladMan.service.user;

import com.kosta.saladMan.dto.store.StoreDto;

import java.util.List;

import org.springframework.data.domain.Page;

public interface StoreService {
    List<StoreDto> getAllStores();
    Page<StoreDto> getStoresByPage(int page, int size); //페이징 처리 
}
