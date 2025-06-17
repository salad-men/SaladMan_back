package com.kosta.saladMan.service.inventory;

import java.util.List;

import com.kosta.saladMan.dto.inventory.HqIngredientDto;
import com.kosta.saladMan.dto.inventory.IngredientCategoryDto;
import com.kosta.saladMan.dto.inventory.IngredientDto;
import com.kosta.saladMan.dto.inventory.StoreIngredientDto;
import com.kosta.saladMan.dto.store.StoreDto;
import com.kosta.saladMan.util.PageInfo;

public interface InventoryService {
    List<HqIngredientDto> searchHqInventory(PageInfo pageInfo, String category, String name);
    List<StoreIngredientDto> searchStoreInventory(PageInfo pageInfo, String store, String category, String name);
    
    void addHqIngredient(HqIngredientDto dto);
    void updateHqIngredient(HqIngredientDto dto);
    
    
    List<IngredientCategoryDto> getAllCategories();
    List<StoreDto>    getAllStores();
    List<IngredientDto> getAllIngredients();

    
}
