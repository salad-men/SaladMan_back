package com.kosta.saladMan.service.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.kosta.saladMan.dto.inventory.IngredientItemDto;
import com.kosta.saladMan.dto.purchaseOrder.LowStockItemDto;
import com.kosta.saladMan.entity.inventory.Ingredient;
import com.kosta.saladMan.repository.inventory.IngredientRepository;
import com.kosta.saladMan.repository.order.IngredientDslRepository;
import com.kosta.saladMan.repository.order.StoreIngredientDslRepository;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private IngredientRepository ingredientRepository;

	@Autowired
	private IngredientDslRepository ingredientDslRepository;

	@Autowired
	private StoreIngredientDslRepository storeIngredientDslRepository;

	// 재료 리스트
	@Override
	public Page<IngredientItemDto> getIngredientList(Boolean available, String category, String keyword, int page,
			int size) throws Exception {
		
		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
		return ingredientDslRepository.ingredientList(available, category, keyword, pageable);
	}

	// 발주가능여부 껏켯
	@Override
	public Boolean toggleIngredientAvailability(Integer id) throws Exception {
		
		Ingredient ingredient = ingredientRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("재료가 존재하지 않습니다."));

		ingredient.setAvailable(!ingredient.getAvailable()); // 반전
		ingredientRepository.save(ingredient);

		return ingredient.getAvailable(); // 변경된 상태 반환
	
	}

	// 수량 미달 확인
	@Override
	public List<LowStockItemDto> getLowStockItems(Integer storeId) {
		
		return storeIngredientDslRepository.findLowStockIngredientsByStore(storeId);
	
	}

}
