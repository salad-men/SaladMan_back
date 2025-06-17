package com.kosta.saladMan.service.user;

import com.kosta.saladMan.dto.store.StoreDto;
import com.kosta.saladMan.entity.store.Store;
import com.kosta.saladMan.repository.StoreRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
        		.filter(store -> store.getRole() != null && !"ROLE_HQ".equals(store.getRole()))
                .map(Store::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<StoreDto> getStoresByPage(int page, int size) {
        Page<Store> storePage = storeRepository.findByRoleNot("ROLE_HQ", PageRequest.of(page, size));
        return storePage.map(StoreDto::fromEntity);
    }

}
