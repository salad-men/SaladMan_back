package com.kosta.saladMan.service.chatbot;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.kosta.saladMan.dto.chatbot.StoreTimeResponseDto;
import com.kosta.saladMan.entity.store.Store;
import com.kosta.saladMan.repository.user.StoreChatBotRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatBotStoreServiceImpl implements ChatBotStoreService {

    private final StoreChatBotRepository storeChatBotRepository;

    @Override
    public List<StoreTimeResponseDto> getStoreTimesByKeyword(String keyword) {
        List<Store> stores = storeChatBotRepository.findByNameContainingOrAddressContaining(keyword, keyword);

        return stores.stream()
            .map(store -> StoreTimeResponseDto.builder()
                .name(store.getName())
                .address(store.getAddress())
                .openTime(store.getOpenTime())
                .closeTime(store.getCloseTime())
                .breakDay(store.getBreakDay())
                .build())
            .collect(Collectors.toList());
    }
}
