package com.kosta.saladMan.service.user;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.kosta.saladMan.dto.store.StoreDto;
import com.kosta.saladMan.entity.store.Store;
import com.kosta.saladMan.repository.user.StoreChatBotRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreChatBotServiceImpl implements StoreChatBotService {

    private final StoreChatBotRepository storeChatBotRepository;

    @Override
    public List<StoreDto> searchStores(String keyword) {
        List<Store> stores = storeChatBotRepository.findByAddressContainingIgnoreCase(keyword);

        return stores.stream()
                .map(StoreDto::fromEntity)
                .collect(Collectors.toList());
    }
}
