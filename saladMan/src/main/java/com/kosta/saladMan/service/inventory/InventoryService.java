package com.kosta.saladMan.service.inventory;

import com.kosta.saladMan.dto.inventory.DisposalDto;
import com.kosta.saladMan.dto.inventory.HqIngredientDto;
import com.kosta.saladMan.dto.inventory.IngredientCategoryDto;
import com.kosta.saladMan.dto.inventory.IngredientDto;
import com.kosta.saladMan.dto.inventory.InventoryRecordDto;
import com.kosta.saladMan.dto.inventory.StoreIngredientDto;
import com.kosta.saladMan.dto.inventory.StoreIngredientSettingDto;
import com.kosta.saladMan.dto.store.StoreDto;
import com.kosta.saladMan.util.PageInfo;

import java.util.List;

public interface InventoryService {

    // 본사 재고 조회 (유통기한 필터 선택적 적용)
	List<HqIngredientDto> getHqInventory(
            Integer storeId, Integer categoryId, String keyword, String startDateStr, String endDateStr, PageInfo pageInfo);

    // 매장 재고 조회 (유통기한 필터 선택적 적용)
	List<StoreIngredientDto> getStoreInventory(
            Integer storeId, Integer categoryId, String keyword, String startDateStr, String endDateStr, PageInfo pageInfo);
    
    // 본사 재고 추가
    void addHqIngredient(HqIngredientDto dto);

    // 본사 재고 수정
    void updateHqIngredient(HqIngredientDto dto);
    
    // 매장 재고 수정
    void updateStoreIngredient(StoreIngredientDto dto);

    // 카테고리 조회
    List<IngredientCategoryDto> getAllCategories();

    // 매장 조회
    List<StoreDto> getAllStores();
    
    //매장 조회(본사제외)
    List<StoreDto> getStoresExceptHQ();

    // 매장 전체 조회
    List<StoreIngredientDto> getAllStoreInventory(
            Integer categoryId, String keyword, String startDateStr, String endDateStr, PageInfo pageInfo);

    // 재료 조회
    List<IngredientDto> getAllIngredients();

    // 폐기 신청 처리(유통기한 조회에서)
    void processDisposalRequest(List<DisposalDto> disposalList);
    
    // 본사 폐기 목록 조회
    List<DisposalDto> searchHqDisposals(PageInfo pageInfo, Integer categoryId, String keyword, String startDate, String endDate);

    // 매장 폐기 목록 조회
    List<DisposalDto> searchStoreDisposals(PageInfo pageInfo, Integer storeId, Integer categoryId, String keyword, String startDate, String endDate);

    // 폐기 승인 (상태 '완료' 변경)
    void approveDisposals(List<Integer> disposalIds);

    // 폐기 반려 (상태 '반려됨' + 반려 사유 저장)
    void rejectDisposals(List<DisposalDto> rejectDtos);
    
    //매장별 재고 설정 조회(본사) 
    List<StoreIngredientSettingDto> getHqSettingsByFilters(Integer storeId, Integer categoryId, String keyword, PageInfo pageInfo);

    //매장별 재고 설정 조회(매장)
    List<StoreIngredientSettingDto> getStoreSettingsByFilters(Integer storeId, Integer categoryId, String keyword, PageInfo pageInfo);

    //매장별 재고 저장
    StoreIngredientSettingDto saveSetting(StoreIngredientSettingDto dto);
    
    //재고 기록 저장
    void addRecord(InventoryRecordDto dto);
    
    //재고기록 조회(출고or입고)
    public List<InventoryRecordDto> getRecordsByStoreAndType(Integer storeId, String changeType, PageInfo pageInfo);

}
