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
        return storeChatBotRepository.findByKeyword(keyword)
            .stream()
            .map(Store::toDto) // Store -> StoreDto 변환
            .collect(Collectors.toList());
    }
}
