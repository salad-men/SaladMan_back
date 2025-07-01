package com.kosta.saladMan.service.chatbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosta.saladMan.entity.chatbot.ChatbotAnswer;
import com.kosta.saladMan.entity.menu.TotalMenu;
import com.kosta.saladMan.repository.chatbot.ChatbotAnswerRepository;
import com.kosta.saladMan.repository.user.MenuIngredientRepository;
import com.kosta.saladMan.repository.user.TotalMenuRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ChatbotAnswerServiceImpl implements ChatbotAnswerService {

    private final ChatbotAnswerRepository chatbotAnswerRepository;
    private final TotalMenuRepository totalMenuRepository;
    private final MenuIngredientRepository menuIngredientRepository;
    
    
    @Override
    public String getAnswerByValueKey(String valueKey) {
        Optional<ChatbotAnswer> answerOpt = chatbotAnswerRepository.findByValueKey(valueKey);
        if (answerOpt.isEmpty()) {
            return "등록된 답변이 없습니다.";
        }

        ChatbotAnswer answer = answerOpt.get();

        if (answer.isDynamic()) {
            if ("vegan".equals(valueKey)) {
                return getVeganSalads();
            } else if ("quantity".equals(valueKey)) {
                return getTopQuantitySalads();
            }
        }

        return answer.getAnswerText();
    }
    
    
    @Override
    public List<Map<String, String>> findMenusByIngredientName(String ingredientName) {
        List<TotalMenu> matchedMenus = menuIngredientRepository.findMenusByIngredientName(ingredientName);

        return matchedMenus.stream().map(menu -> {
            Map<String, String> map = new HashMap<>();
            map.put("name", menu.getName());
            map.put("description", menu.getDescription() != null ? menu.getDescription() : "");
            return map;
        }).collect(Collectors.toList());
    }

    
    //비건 검색 결과
    private String getVeganSalads() {
        List<TotalMenu> veganMenus = totalMenuRepository.findByCategoryId(3);

        List<Map<String, Object>> result = new ArrayList<>();
        for (TotalMenu menu : veganMenus) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", menu.getName());
            item.put("description", menu.getDescription());
            result.add(item);
        }

        try {
            return new ObjectMapper().writeValueAsString(result);
        } catch (JsonProcessingException e) {
            return "비건 샐러드 정보를 불러오지 못했습니다.";
        }
    }
    
    
    
    //양 많은 순 TOP3 검색 결과    
    private String getTopQuantitySalads() {
        List<Object[]> resultList = menuIngredientRepository.findTop3SaladsByQuantity(PageRequest.of(0, 3));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : resultList) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", row[0]);         // 메뉴 이름
            item.put("totalQuantity", row[1]); // 총 재료 양
            result.add(item);
        }

        try {
            return new ObjectMapper().writeValueAsString(result);
        } catch (JsonProcessingException e) {
            return "샐러드 데이터를 불러오지 못했습니다.";
        }
    }
    
    
}
	