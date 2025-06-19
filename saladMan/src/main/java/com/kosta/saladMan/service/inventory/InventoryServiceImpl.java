package com.kosta.saladMan.service.inventory;

import com.kosta.saladMan.dto.inventory.DisposalDto;
import com.kosta.saladMan.dto.inventory.HqIngredientDto;
import com.kosta.saladMan.dto.inventory.IngredientCategoryDto;
import com.kosta.saladMan.dto.inventory.IngredientDto;
import com.kosta.saladMan.dto.inventory.InventoryRecordDto;
import com.kosta.saladMan.dto.inventory.StoreIngredientDto;
import com.kosta.saladMan.dto.inventory.StoreIngredientSettingDto;
import com.kosta.saladMan.dto.store.StoreDto;
import com.kosta.saladMan.entity.inventory.Disposal;
import com.kosta.saladMan.entity.inventory.HqIngredient;
import com.kosta.saladMan.entity.inventory.Ingredient;
import com.kosta.saladMan.entity.inventory.IngredientCategory;
import com.kosta.saladMan.entity.inventory.InventoryRecord;
import com.kosta.saladMan.entity.inventory.StoreIngredient;
import com.kosta.saladMan.entity.inventory.StoreIngredientSetting;
import com.kosta.saladMan.entity.store.Store;
import com.kosta.saladMan.repository.inventory.DisposalRepository;
import com.kosta.saladMan.repository.inventory.HqIngredientRepository;
import com.kosta.saladMan.repository.inventory.HqInventoryDslRepository;
import com.kosta.saladMan.repository.inventory.StoreInventoryDslRepository;
import com.kosta.saladMan.repository.StoreRepository;
import com.kosta.saladMan.repository.inventory.IngredientCategoryRepository;
import com.kosta.saladMan.repository.inventory.IngredientRepository;
import com.kosta.saladMan.repository.inventory.InventoryRecordRepository;
import com.kosta.saladMan.repository.inventory.StoreIngredientRepository;
import com.kosta.saladMan.repository.inventory.StoreIngredientSettingRepository;
import com.kosta.saladMan.util.PageInfo;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private static final int PAGE_SIZE = 10;

    private final HqInventoryDslRepository hqInventoryDslRepository;
    private final StoreInventoryDslRepository storeInventoryDslRepository;
    private final HqIngredientRepository hqIngredientRepository;
    private final StoreIngredientRepository storeIngredientRepository;
    private final DisposalRepository disposalRepository;
    private final IngredientCategoryRepository categoryRepository;
    private final StoreRepository storeRepository;
    private final IngredientRepository ingredientRepository;
    private final StoreIngredientSettingRepository storeIngredientSettingRepository;
    
    private final InventoryRecordRepository recordRepository;

    

    // 본사 재고(전체/유통기한) 조회
    public List<HqIngredientDto> getHqInventory(
            Integer storeId, String category, String keyword, String startDateStr, String endDateStr, PageInfo pageInfo) {
        // 본사만
        if (storeId != null && storeId != 1) return List.of();

        LocalDate startDate = (startDateStr == null || startDateStr.isBlank()) ? null : LocalDate.parse(startDateStr);
        LocalDate endDate = (endDateStr == null || endDateStr.isBlank()) ? null : LocalDate.parse(endDateStr);

        PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, PAGE_SIZE);
        long totalCount = hqInventoryDslRepository.countHqInventoryByFilters(category, keyword, startDate, endDate);
        pageInfo.setAllPage((int) Math.ceil((double) totalCount / PAGE_SIZE));
        int start = (pageInfo.getCurPage() - 1) / 10 * 10 + 1;
        int end = Math.min(start + 9, pageInfo.getAllPage());
        pageInfo.setStartPage(start);
        pageInfo.setEndPage(end);

        List<HqIngredientDto> list = hqInventoryDslRepository.findHqInventoryByFilters(category, keyword, startDate, endDate, pageRequest);
        
        // 본사 Store 엔티티 조회해서 이름 직접 세팅
        Store hqStore = storeRepository.findById(1) // 본사 ID가 1이라 가정
                .orElseThrow(() -> new IllegalArgumentException("본사 매장을 찾을 수 없습니다."));
        String hqStoreName = hqStore.getName();

        list.forEach(dto -> dto.setStoreName(hqStoreName));

        return list;
    }

    // 매장 재고(전체/유통기한) 조회
    public List<StoreIngredientDto> getStoreInventory(
            Integer storeId, String category, String keyword, String startDateStr, String endDateStr, PageInfo pageInfo) {
        if (storeId == null || storeId == 1) return List.of();

        LocalDate startDate = (startDateStr == null || startDateStr.isBlank()) ? null : LocalDate.parse(startDateStr);
        LocalDate endDate = (endDateStr == null || endDateStr.isBlank()) ? null : LocalDate.parse(endDateStr);

        PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, PAGE_SIZE);
        Store storeEntity = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 매장 ID: " + storeId));

        long totalCount = storeInventoryDslRepository.countStoreInventoryByFilters(storeEntity.getName(), category, keyword, startDate, endDate);
        pageInfo.setAllPage((int) Math.ceil((double) totalCount / PAGE_SIZE));
        int start = (pageInfo.getCurPage() - 1) / 10 * 10 + 1;
        int end = Math.min(start + 9, pageInfo.getAllPage());
        pageInfo.setStartPage(start);
        pageInfo.setEndPage(end);

        return storeInventoryDslRepository.findStoreInventoryByFilters(
                storeEntity.getName(), category, keyword, startDate, endDate, pageRequest)
                .stream().map(StoreIngredient::toDto).collect(Collectors.toList());
    }


    //폐기신청
    @Override
    @Transactional
    public void processDisposalRequest(List<DisposalDto> disposalList) {
        // 본사 Store 엔티티 조회 (ID로 고정)
        Store hqStore = storeRepository.findByName("본사계정")
                .orElseThrow(() -> new IllegalArgumentException("본사 매장을 찾을 수 없습니다."));
        
        Integer hqStoreId = hqStore.getId();

        for (DisposalDto item : disposalList) {
            Integer storeId = item.getStoreId();

            if (storeId.equals(hqStoreId)) {
                // 본사 재고 처리
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

            } else {
                // 매장 재고 처리
                Store store = storeRepository.findById(storeId)
                        .orElseThrow(() -> new IllegalArgumentException("매장 ID " + storeId + " 를 찾을 수 없습니다."));

                StoreIngredient storeIngredient = storeIngredientRepository.findById(item.getId())
                        .orElseThrow(() -> new IllegalArgumentException("재고 ID " + item.getId() + " 를 찾을 수 없습니다."));

                int currentQty = storeIngredient.getQuantity() == null ? 0 : storeIngredient.getQuantity();
                int disposalAmount = item.getQuantity();

                if (disposalAmount <= 0) {
                    throw new IllegalArgumentException("폐기량은 0보다 커야 합니다. 재고 ID: " + item.getId());
                }
                if (disposalAmount > currentQty) {
                    throw new IllegalArgumentException("폐기량이 현재 재고량보다 많습니다. 재고 ID: " + item.getId());
                }

                storeIngredient.setQuantity(currentQty - disposalAmount);
                storeIngredientRepository.save(storeIngredient);

                Disposal disposal = Disposal.builder()
                        .ingredient(storeIngredient.getIngredient())
                        .store(store) 
                        .quantity(disposalAmount)
                        .status("신청")
                        .requestedAt(LocalDate.now())
                        .memo("유통기한 초과로 폐기 신청")
                        .build();

                disposalRepository.save(disposal);
            }
        }
    }
    

    //재고 추가
    @Override
    public void addHqIngredient(HqIngredientDto dto) {
        // 유효성 검사: categoryId, ingredientId 존재 확인
        IngredientCategory category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 ID: " + dto.getCategoryId()));
        Ingredient ingredient = ingredientRepository.findById(dto.getIngredientId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 재료 ID: " + dto.getIngredientId()));

        HqIngredient entity = dto.toEntity();

        hqIngredientRepository.save(entity);
    }


    //재고 수정정
    @Override
    public void updateHqIngredient(HqIngredientDto dto) {
        hqInventoryDslRepository.updateHqIngredient(dto);
    }

    
 // 본사 폐기 목록 조회
    @Override
    public List<DisposalDto> searchHqDisposals(PageInfo pageInfo,String category, String keyword, String startDateStr, String endDateStr) {
        LocalDate startDate = (startDateStr != null && !startDateStr.isBlank()) ? LocalDate.parse(startDateStr) : null;
        LocalDate endDate = (endDateStr != null && !endDateStr.isBlank()) ? LocalDate.parse(endDateStr) : null;
        String store="본사";
        
        int totalCount = hqInventoryDslRepository.countHqDisposals(store, category, keyword, startDate, endDate);
        pageInfo.setAllPage((int)Math.ceil((double)totalCount / 10));   
        int block = 5; 
        int curPage = pageInfo.getCurPage() == null ? 1 : pageInfo.getCurPage();
        int allPage = pageInfo.getAllPage() == null ? 1 : pageInfo.getAllPage();

        // 블록 계산
        int startPage = ((curPage - 1) / block) * block + 1;
        int endPage = Math.min(startPage + block - 1, allPage);

        pageInfo.setStartPage(startPage);
        pageInfo.setEndPage(endPage);

        // 목록 조회
        List<Disposal> result = hqInventoryDslRepository.selectHqDisposalListByFiltersPaging(
            store, category, keyword, startDate, endDate,
            PageRequest.of(curPage - 1, 10)
        );

        return result.stream().map(Disposal::toDto).collect(Collectors.toList());
        
    }

    // 매장 폐기 목록 조회
    @Override
    public List<DisposalDto> searchStoreDisposals(PageInfo pageInfo, String store, String category, String keyword, String startDateStr, String endDateStr) {
        LocalDate startDate = (startDateStr != null && !startDateStr.isBlank()) ? LocalDate.parse(startDateStr) : null;
        LocalDate endDate = (endDateStr != null && !endDateStr.isBlank()) ? LocalDate.parse(endDateStr) : null;

        int totalCount = storeInventoryDslRepository.countStoreDisposals(store, category, keyword, startDate, endDate);
        pageInfo.setAllPage((int)Math.ceil((double)totalCount / 10));
        int block = 5;
        int curPage = pageInfo.getCurPage() == null ? 1 : pageInfo.getCurPage();
        int allPage = pageInfo.getAllPage() == null ? 1 : pageInfo.getAllPage();

        int startPage = ((curPage - 1) / block) * block + 1;
        int endPage = Math.min(startPage + block - 1, allPage);

        pageInfo.setStartPage(startPage);
        pageInfo.setEndPage(endPage);

        List<Disposal> result = storeInventoryDslRepository.selectStoreDisposalListByFiltersPaging(
            store, category, keyword, startDate, endDate,
            PageRequest.of(curPage - 1, 10)
        );

        return result.stream().map(Disposal::toDto).collect(Collectors.toList());
    }


    // 폐기 승인
    @Override
    @Transactional
    public void approveDisposals(List<Integer> disposalIds) {
        hqInventoryDslRepository.updateDisposalStatus(disposalIds, "완료", null);
    }

    // 폐기 반려
    @Override
    @Transactional
    public void rejectDisposals(List<DisposalDto> rejectDtos) {
        for (DisposalDto dto : rejectDtos) {
            hqInventoryDslRepository.updateDisposalStatus(List.of(dto.getId()), "반려됨", dto.getMemo());
        }
    }
    
    
    //카테고리 가져오기
    @Override
    public List<IngredientCategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(IngredientCategory::toDto)
                .collect(Collectors.toList());
    }
    
    //매장 가져오기
    @Override
    public List<StoreDto> getAllStores() {
        return storeRepository.findAll().stream()
                .map(Store::toDto)
                .collect(Collectors.toList());
    }

    //재료설정 조회
    @Override
    public List<StoreIngredientSettingDto> getSettingsByStoreId(Integer storeId) {
        return storeIngredientSettingRepository.findByStoreId(storeId)
                .stream()
                .map(StoreIngredientSetting::toDto)  
                .collect(Collectors.toList());
    }


    
    //재료설정 저장(추가/수정)
    @Transactional
    @Override
    public StoreIngredientSettingDto saveSetting(StoreIngredientSettingDto dto) {
        StoreIngredientSetting entity = storeIngredientSettingRepository.findByStoreIdAndIngredientId(dto.getStoreId(), dto.getIngredientId())
                .map(existing -> {
                    existing.setMinQuantity(dto.getMinQuantity());
                    existing.setMaxQuantity(dto.getMaxQuantity());
                    return existing;
                })
                .orElse(dto.toEntity());

        StoreIngredientSetting saved = storeIngredientSettingRepository.save(entity);

        return saved.toDto();   
    }

    @Override
    public List<IngredientDto> getAllIngredients() {
        List<Ingredient> ingredients = hqInventoryDslRepository.getAllIngredients();

        return ingredients.stream()
                .map(Ingredient::toDto)   
                .collect(Collectors.toList());
    }
    
    
    @Override
    public void addRecord(InventoryRecordDto dto) {
        Ingredient ingredient = ingredientRepository.findById(dto.getIngredientId())
                .orElseThrow(() -> new IllegalArgumentException("재료 ID 오류: " + dto.getIngredientId()));

        Store store = storeRepository.findById(dto.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("매장 ID 오류: " + dto.getStoreId()));

        InventoryRecord record = dto.toEntity(ingredient, store);
        recordRepository.save(record);
    }

    @Override
    public List<InventoryRecordDto> getRecordsByStoreAndType(Integer storeId, String type) {
        List<InventoryRecord> records = recordRepository.findByStoreIdAndChangeType(storeId, type);
        return records.stream().map(InventoryRecord::toDto).collect(Collectors.toList());
    }
}