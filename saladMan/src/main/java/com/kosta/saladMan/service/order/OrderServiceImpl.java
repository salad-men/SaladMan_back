package com.kosta.saladMan.service.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.kosta.saladMan.dto.inventory.IngredientItemDto;
import com.kosta.saladMan.entity.inventory.Ingredient;
import com.kosta.saladMan.repository.inventory.IngredientRepository;
import com.kosta.saladMan.repository.order.IngredientDslRepository;

@Service
public class OrderServiceImpl implements OrderService {
	@Autowired
	private IngredientDslRepository ingredientDslRepository;
	@Autowired
	private IngredientRepository ingredientRepository;

	@Override
	public Page<IngredientItemDto> getIngredientList(Boolean available, String category, String keyword, int page, int size) throws Exception {
		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
		return ingredientDslRepository.ingredientList(available,category,keyword, pageable);
	}

	@Override
	public Boolean toggleIngredientAvailability(Integer id) throws Exception {
	    Ingredient ingredient = ingredientRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("재료가 존재하지 않습니다."));

	        ingredient.setAvailable(!ingredient.getAvailable()); // 반전
	        ingredientRepository.save(ingredient);

	        return ingredient.getAvailable(); // 변경된 상태 반환
	}

}
