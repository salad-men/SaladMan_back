package com.kosta.saladMan.service.inventory;

import com.kosta.saladMan.dto.inventory.DisposalDto;
import com.kosta.saladMan.dto.inventory.HqIngredientDto;
import com.kosta.saladMan.dto.inventory.StoreIngredientDto;
import com.kosta.saladMan.util.PageInfo;

import java.util.List;

public interface InventoryService {

    // 본사 재고 조회 (유통기한 필터 선택적 적용)
    List<HqIngredientDto> searchHqInventory(PageInfo pageInfo, String category, String name, String startDate, String endDate);

    // 매장 재고 조회 (유통기한 필터 선택적 적용)
    List<StoreIngredientDto> searchStoreInventory(PageInfo pageInfo, String store, String category, String name, String startDate, String endDate);

    // 본사 재고 추가
    void addHqIngredient(HqIngredientDto dto);

    // 본사 재고 수정
    void updateHqIngredient(HqIngredientDto dto);

    // 카테고리 조회
    List<com.kosta.saladMan.dto.inventory.IngredientCategoryDto> getAllCategories();

    // 매장 조회
    List<com.kosta.saladMan.dto.store.StoreDto> getAllStores();

    // 재료 조회
    List<com.kosta.saladMan.dto.inventory.IngredientDto> getAllIngredients();

    // 폐기 신청 처리
    void processDisposalRequest(List<DisposalDto> disposalList);
}
