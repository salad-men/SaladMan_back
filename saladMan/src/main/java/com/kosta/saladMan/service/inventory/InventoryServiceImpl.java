package com.kosta.saladMan.service.inventory;

import com.kosta.saladMan.dto.inventory.HqIngredientDto;
import com.kosta.saladMan.dto.inventory.IngredientCategoryDto;
import com.kosta.saladMan.dto.inventory.IngredientDto;
import com.kosta.saladMan.dto.inventory.StoreIngredientDto;
import com.kosta.saladMan.dto.store.StoreDto;
import com.kosta.saladMan.entity.inventory.HqIngredient;
import com.kosta.saladMan.entity.inventory.Ingredient;
import com.kosta.saladMan.entity.inventory.IngredientCategory;
import com.kosta.saladMan.entity.inventory.StoreIngredient;
import com.kosta.saladMan.entity.store.Store;
import com.kosta.saladMan.repository.StoreRepository;
import com.kosta.saladMan.repository.inventory.HqIngredientRepository;
import com.kosta.saladMan.repository.inventory.IngredientCategoryRepository;
import com.kosta.saladMan.repository.inventory.IngredientRepository;
import com.kosta.saladMan.repository.inventory.InventoryDslRepository;
import com.kosta.saladMan.repository.inventory.StoreIngredientRepository;
import com.kosta.saladMan.util.PageInfo;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private static final int PAGE_SIZE = 10;
    
    private final InventoryDslRepository dsl;
    private final HqIngredientRepository hqRepo;
    private final StoreIngredientRepository storeRepo;
    private final IngredientRepository ingredientRepo;

    
    private final IngredientCategoryRepository  categoryRepo;
    private final StoreRepository               storeMetaRepo;

    @Override
    public List<HqIngredientDto> searchHqInventory(PageInfo pageInfo, String category, String name) {
        PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, PAGE_SIZE);
        long total = dsl.selectHqCount(category, name);
        pageInfo.setAllPage((int) Math.ceil((double) total / PAGE_SIZE));
        int start = (pageInfo.getCurPage() - 1) / 10 * 10 + 1;
        int end = Math.min(start + 9, pageInfo.getAllPage());
        pageInfo.setStartPage(start);
        pageInfo.setEndPage(end);

        List<HqIngredient> entities = dsl.selectHqListByPaging(pageRequest, category, name);
        return entities.stream()
            .map(entity -> {
                HqIngredientDto dto = entity.toDto();
                dto.setCategoryName(entity.getCategory().getName());
                dto.setIngredientName(entity.getIngredient().getName());
                return dto;
            })
            .collect(Collectors.toList());
    }


//    @Override
//    @Transactional
//    public void addHqIngredient(HqIngredientDto dto) {
//        // save 방식: transient 엔티티로 ID만 세팅 후 save()
//        hqRepo.save(HqIngredient.builder()
//                .category(IngredientCategory.builder().id(dto.getCategoryId()).build())
//                .ingredient(Ingredient.builder().id(dto.getIngredientId()).build())
//                .quantity(dto.getQuantity())
//                .minimumOrderUnit(dto.getMinimumOrderUnit())
//                .unitCost(dto.getUnitCost())
//                .expiredQuantity(dto.getExpiredQuantity())
//                .expiredDate(dto.getExpiredDate())
//                .build());
//    }
    
    @Override
    @Transactional
    public void addHqIngredient(HqIngredientDto dto) {
        // INSERT 시 연관 엔티티를 영속화된 상태로 조회하여 세팅
        IngredientCategory category = categoryRepo.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 ID: " + dto.getCategoryId()));
        Ingredient ingredient = ingredientRepo.findById(dto.getIngredientId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 재료 ID: " + dto.getIngredientId()));

        HqIngredient entity = HqIngredient.builder()
                .category(category)
                .ingredient(ingredient)
                .quantity(dto.getQuantity())
                .minimumOrderUnit(dto.getMinimumOrderUnit())
                .unitCost(dto.getUnitCost())
                .expiredQuantity(dto.getExpiredQuantity())
                .expiredDate(dto.getExpiredDate())
                .build();

        hqRepo.save(entity);
    }
    
    @Override
    public List<StoreIngredientDto> searchStoreInventory(PageInfo pageInfo, String store, String category, String name) {
        PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, PAGE_SIZE);
        long total = dsl.selectStoreCount(store, category, name);
        pageInfo.setAllPage((int) Math.ceil((double) total / PAGE_SIZE));
        int start = (pageInfo.getCurPage() - 1) / 10 * 10 + 1;
        int end = Math.min(start + 9, pageInfo.getAllPage());
        pageInfo.setStartPage(start);
        pageInfo.setEndPage(end);

        List<StoreIngredient> entities = dsl.selectStoreListByPaging(pageRequest, store, category, name);
        return entities.stream()
                .map(StoreIngredient::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void updateHqIngredient(HqIngredientDto dto) {
        dsl.updateHqIngredient(dto);
    }
    
    
    //카테고리
    @Override
    public List<IngredientCategoryDto> getAllCategories() {
        return categoryRepo.findAll().stream()
                   .map(IngredientCategory::toDto)
                   .collect(Collectors.toList());
    }
    //매장(추후 매장레포짓에서 가져오기)
    @Override
    public List<StoreDto> getAllStores() {
        return storeMetaRepo.findAll().stream()
                   .map(Store::toDto)
                   .collect(Collectors.toList());
    }
    
    @Override
    public List<IngredientDto> getAllIngredients() {
        return ingredientRepo.findAll().stream()
                .map(Ingredient::toDto)
                .collect(Collectors.toList());
    }
}