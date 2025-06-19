package com.kosta.saladMan.service.order;

import org.springframework.data.domain.Page;

import com.kosta.saladMan.dto.inventory.IngredientItemDto;

public interface OrderService {
    Page<IngredientItemDto> getIngredientList(Boolean available, int page, int size) throws Exception;
    Boolean toggleIngredientAvailability(Integer id) throws Exception;
}
