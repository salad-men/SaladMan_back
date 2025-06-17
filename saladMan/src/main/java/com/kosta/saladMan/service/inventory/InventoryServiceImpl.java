package com.kosta.saladMan.service.inventory;

import com.kosta.saladMan.dto.inventory.DisposalDto;
import com.kosta.saladMan.dto.inventory.HqIngredientDto;
import com.kosta.saladMan.dto.inventory.StoreIngredientDto;
import com.kosta.saladMan.entity.inventory.Disposal;
import com.kosta.saladMan.entity.inventory.HqIngredient;
import com.kosta.saladMan.entity.inventory.Ingredient;
import com.kosta.saladMan.entity.inventory.IngredientCategory;
import com.kosta.saladMan.entity.store.Store;
import com.kosta.saladMan.repository.inventory.DisposalRepository;
import com.kosta.saladMan.repository.inventory.HqIngredientRepository;
import com.kosta.saladMan.repository.inventory.HqInventoryDslRepository;
import com.kosta.saladMan.repository.inventory.StoreInventoryDslRepository;
import com.kosta.saladMan.repository.StoreRepository;
import com.kosta.saladMan.repository.inventory.IngredientCategoryRepository;
import com.kosta.saladMan.repository.inventory.IngredientRepository;
import com.kosta.saladMan.util.PageInfo;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private static final int PAGE_SIZE = 10;

    private final HqInventoryDslRepository hqInventoryDslRepository;
    private final StoreInventoryDslRepository storeInventoryDslRepository;
    private final HqIngredientRepository hqIngredientRepository;
    private final DisposalRepository disposalRepository;
    private final IngredientCategoryRepository categoryRepository;
    private final StoreRepository storeRepository;
    private final IngredientRepository ingredientRepository;

    @Override
    public List<HqIngredientDto> searchHqInventory(PageInfo pageInfo, String category, String name, String startDateStr, String endDateStr) {
        LocalDate startDate = (startDateStr == null || startDateStr.isBlank()) ? null : LocalDate.parse(startDateStr);
        LocalDate endDate = (endDateStr == null || endDateStr.isBlank()) ? null : LocalDate.parse(endDateStr);

        System.out.println("searchHqInventory called with: category=" + category + ", keyword=" + name + ", startDate=" + startDate + ", endDate=" + endDate);

        PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, PAGE_SIZE);

        long totalCount = hqInventoryDslRepository.selectHqCountByExpirationFilters(category, name, startDate, endDate);
        System.out.println("totalCount=" + totalCount);
        pageInfo.setAllPage((int) Math.ceil((double) totalCount / PAGE_SIZE));
        int start = (pageInfo.getCurPage() - 1) / 10 * 10 + 1;
        int end = Math.min(start + 9, pageInfo.getAllPage());
        pageInfo.setStartPage(start);
        pageInfo.setEndPage(end);

        List<HqIngredient> entities = hqInventoryDslRepository.selectHqListByExpirationFiltersPaging(category, name, startDate, endDate, pageRequest);
        System.out.println("entities.size=" + entities.size());

        return entities.stream()
                .map(entity -> {
                    HqIngredientDto dto = entity.toDto();
                    dto.setStoreName("본사");  
                    return dto;
                })
                .collect(Collectors.toList());
    }


    @Override
    public List<StoreIngredientDto> searchStoreInventory(PageInfo pageInfo, String store, String category, String name, String startDateStr, String endDateStr) {
        LocalDate startDate = (startDateStr == null || startDateStr.isBlank()) ? null : LocalDate.parse(startDateStr);
        LocalDate endDate = (endDateStr == null || endDateStr.isBlank()) ? null : LocalDate.parse(endDateStr);

        System.out.println("searchStoreInventory called with: store=" + store + ", category=" + category + ", keyword=" + name + ", startDate=" + startDate + ", endDate=" + endDate);

        PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, PAGE_SIZE);

        long totalCount = storeInventoryDslRepository.selectStoreCountByExpirationFilters(store, category, name, startDate, endDate);
        System.out.println("totalCount=" + totalCount);
        pageInfo.setAllPage((int) Math.ceil((double) totalCount / PAGE_SIZE));
        int start = (pageInfo.getCurPage() - 1) / 10 * 10 + 1;
        int end = Math.min(start + 9, pageInfo.getAllPage());
        pageInfo.setStartPage(start);
        pageInfo.setEndPage(end);

        List<com.kosta.saladMan.entity.inventory.StoreIngredient> entities = storeInventoryDslRepository.selectStoreListByExpirationFiltersPaging(store, category, name, startDate, endDate, pageRequest);
        System.out.println("entities.size=" + entities.size());

        return entities.stream()
                .map(com.kosta.saladMan.entity.inventory.StoreIngredient::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void processDisposalRequest(List<DisposalDto> disposalList) {

    	Store hqStore = storeRepository.findByName("본사계정")
                .orElseThrow(() -> new IllegalArgumentException("본사 매장을 찾을 수 없습니다."));
    	
    	for (DisposalDto item : disposalList) {
            HqIngredient hqIngredient = hqIngredientRepository.findById(item.getId())
                    .orElseThrow(() -> new IllegalArgumentException("재고 ID " + item.getId() + " 를 찾을 수 없습니다."));

            int currentQty = hqIngredient.getQuantity() == null ? 0 : hqIngredient.getQuantity();
            int disposalAmount = item.getQuantity();

            if (disposalAmount <= 0) {
                throw new IllegalArgumentException("폐기량은 0보다 커야 합니다. 재고 ID: " + item.getId());
            }
            if (disposalAmount > currentQty) {
                throw new IllegalArgumentException("폐기량이 현재 재고량보다 많습니다. 재고 ID: " + item.getId());
            }

            hqIngredient.setQuantity(currentQty - disposalAmount);
            hqIngredientRepository.save(hqIngredient);

            Disposal disposal = Disposal.builder()
                    .ingredient(hqIngredient.getIngredient())
                    .store(hqStore)
                    .quantity(disposalAmount)
                    .status("신청")
                    .requestedAt(LocalDate.now())
                    .memo("유통기한 초과로 폐기 신청")
                    .build();

            disposalRepository.save(disposal);
        }
    }

    @Override
    public void addHqIngredient(HqIngredientDto dto) {
        IngredientCategory category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 ID: " + dto.getCategoryId()));
        Ingredient ingredient = ingredientRepository.findById(dto.getIngredientId())
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

        hqIngredientRepository.save(entity);
    }

    @Override
    public void updateHqIngredient(HqIngredientDto dto) {
        hqInventoryDslRepository.updateHqIngredient(dto);
    }

    @Override
    public List<com.kosta.saladMan.dto.inventory.IngredientCategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(com.kosta.saladMan.entity.inventory.IngredientCategory::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<com.kosta.saladMan.dto.store.StoreDto> getAllStores() {
        return storeRepository.findAll().stream()
                .map(com.kosta.saladMan.entity.store.Store::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<com.kosta.saladMan.dto.inventory.IngredientDto> getAllIngredients() {
        return ingredientRepository.findAll().stream()
                .map(com.kosta.saladMan.entity.inventory.Ingredient::toDto)
                .collect(Collectors.toList());
    }
}
