package com.kosta.saladMan.service;

import com.kosta.saladMan.dto.store.StoreDto;
import com.kosta.saladMan.entity.store.Store;
import com.kosta.saladMan.repository.StoreRepository;
import com.kosta.saladMan.service.StoreService;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;

    public StoreServiceImpl(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @Override
    public List<StoreDto> getAllStores() {
        return storeRepository.findAll().stream()
                .map(Store::toDto)
                .collect(Collectors.toList());
    }
}
