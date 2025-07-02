package com.kosta.saladMan.service.menu;

import java.util.List;

import com.kosta.saladMan.dto.inventory.IngredientDto;
import com.kosta.saladMan.dto.menu.IngredientInfoDto;
import com.kosta.saladMan.dto.menu.RecipeDto;
import com.kosta.saladMan.dto.menu.StoreMenuStatusDto;
import com.kosta.saladMan.dto.menu.TotalMenuDto;
import com.kosta.saladMan.util.PageInfo;

public interface StoreMenuService {
	List<TotalMenuDto> getTotalMenu(PageInfo pageInfo, String sort) throws Exception;
	List<StoreMenuStatusDto> getMenuStatus(Integer storeId) throws Exception;
	boolean toggleMenuStatus(Integer storeId, Integer menuId) throws Exception;
	List<RecipeDto> getAllMenuRecipes(PageInfo pageInfo) throws Exception;
	List<IngredientDto> getAllIngredients() throws Exception;
	List<IngredientInfoDto> getIngredientInfo() throws Exception;
}
