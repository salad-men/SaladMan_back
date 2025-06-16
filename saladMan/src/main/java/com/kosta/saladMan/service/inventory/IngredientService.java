package com.kosta.saladMan.service.inventory;

import java.util.List;

import com.kosta.saladMan.dto.inventory.IngredientDto;
import com.kosta.saladMan.util.PageInfo;

public interface IngredientService {
    List<String> getAllCategories();
    List<String> getAllStores();
    List<IngredientDto> searchIngredientList(PageInfo pageInfo, String store, String category, String name) throws Exception;

}
