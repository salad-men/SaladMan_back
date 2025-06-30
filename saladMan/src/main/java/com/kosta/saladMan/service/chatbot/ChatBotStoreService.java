package com.kosta.saladMan.service.chatbot;

import java.util.List;

import com.kosta.saladMan.dto.chatbot.StoreTimeResponseDto;

public interface ChatBotStoreService {
	List<StoreTimeResponseDto> getStoreTimesByKeyword(String keyword);
}
