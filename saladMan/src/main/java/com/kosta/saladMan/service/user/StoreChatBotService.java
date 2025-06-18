package com.kosta.saladMan.service.user;

import java.util.List;

import com.kosta.saladMan.dto.store.StoreDto;

public interface StoreChatBotService {
    List<StoreDto> searchStores(String keyword);
}
