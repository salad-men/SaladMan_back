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
import java.time.LocalDateTime;
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

    
    @Override
    public List<HqIngredientDto> getHqInventory(Integer storeId, Integer categoryId, String keyword, String startDateStr, String endDateStr, PageInfo pageInfo) {
        LocalDate startDate = (startDateStr == null || startDateStr.isBlank()) ? null : LocalDate.parse(startDateStr);
        LocalDate endDate = (endDateStr == null || endDateStr.isBlank()) ? null : LocalDate.parse(endDateStr);

        PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, PAGE_SIZE);
        long totalCount = hqInventoryDslRepository.countHqInventoryByFilters(categoryId, keyword, startDate, endDate);
        pageInfo.setAllPage((int) Math.ceil((double) totalCount / PAGE_SIZE));

        int start = (pageInfo.getCurPage() - 1) / 10 * 10 + 1;
        int end = Math.min(start + 9, pageInfo.getAllPage());
        pageInfo.setStartPage(start);
        pageInfo.setEndPage(end);

        List<HqIngredientDto> list = hqInventoryDslRepository.findHqInventoryByFilters(categoryId, keyword, startDate, endDate, pageRequest);

        Store hqStore = storeRepository.findById(1)
                .orElseThrow(() -> new IllegalArgumentException("본사 매장을 찾을 수 없습니다."));
        String hqStoreName = hqStore.getName();

        list.forEach(dto -> dto.setStoreName(hqStoreName));

        return list;
    }


    @Override
    public List<StoreIngredientDto> getStoreInventory(
            Integer storeId, Integer categoryId, String keyword, String startDateStr, String endDateStr, PageInfo pageInfo) {
        if (storeId == null || storeId == 1) return List.of();

        LocalDate startDate = (startDateStr == null || startDateStr.isBlank()) ? null : LocalDate.parse(startDateStr);
        LocalDate endDate = (endDateStr == null || endDateStr.isBlank()) ? null : LocalDate.parse(endDateStr);

        PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, PAGE_SIZE);
        Store storeEntity = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 매장 ID: " + storeId));

        long totalCount = storeInventoryDslRepository.countStoreInventoryByFilters(
                storeEntity.getId(), categoryId, keyword, startDate, endDate);
        pageInfo.setAllPage((int) Math.ceil((double) totalCount / PAGE_SIZE));
        int start = (pageInfo.getCurPage() - 1) / 10 * 10 + 1;
        int end = Math.min(start + 9, pageInfo.getAllPage());
        pageInfo.setStartPage(start);
        pageInfo.setEndPage(end);

        return storeInventoryDslRepository.findStoreInventoryByFilters(
                storeEntity.getId(), categoryId, keyword, startDate, endDate, pageRequest);
    }

    
    @Override
    public List<StoreIngredientDto> getAllStoreInventory(
            Integer categoryId, String keyword, String startDateStr, String endDateStr, PageInfo pageInfo) {

        LocalDate startDate = (startDateStr == null || startDateStr.isBlank()) ? null : LocalDate.parse(startDateStr);
        LocalDate endDate = (endDateStr == null || endDateStr.isBlank()) ? null : LocalDate.parse(endDateStr);

        long totalCount = storeInventoryDslRepository.countStoreInventoryByFilters(
                null, categoryId, keyword, startDate, endDate);
        pageInfo.setAllPage((int) Math.ceil((double) totalCount / PAGE_SIZE));
        int start = (pageInfo.getCurPage() - 1) / 10 * 10 + 1;
        int end = Math.min(start + 9, pageInfo.getAllPage());
        pageInfo.setStartPage(start);
        pageInfo.setEndPage(end);

        return storeInventoryDslRepository.findStoreInventoryByFilters(
                null, categoryId, keyword, startDate, endDate,
                PageRequest.of(pageInfo.getCurPage() - 1, PAGE_SIZE));
    }


    //재고 추가
    @Override
    public void addHqIngredient(HqIngredientDto dto) {
        IngredientCategory category = categoryRepository.findById(dto.getCategoryId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 ID: " + dto.getCategoryId()));
        Ingredient ingredient = ingredientRepository.findById(dto.getIngredientId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 재료 ID: " + dto.getIngredientId()));

        // 실제 DB에서 Store 조회
        Store store = storeRepository.findById(dto.getStoreId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 매장 ID: " + dto.getStoreId()));

        HqIngredient entity = dto.toEntity();

        entity.setStore(store);
        // 재고 저장
        hqIngredientRepository.save(entity);
        
        // 입고기록 추가
        InventoryRecord record = InventoryRecord.builder()
            .ingredient(ingredient)
            .store(store)
            .quantity(dto.getQuantity())
            .changeType("입고")
            .memo("신규 입고")
            .date(LocalDateTime.now())
            .build();
        recordRepository.save(record);
    }


    //본사 재고 수정
    @Override
    @Transactional
    public void updateHqIngredient(HqIngredientDto dto) {
        hqInventoryDslRepository.updateHqIngredient(dto);
    }
    
    //매장 재고 수정
    @Override
    @Transactional
    public void updateStoreIngredient(StoreIngredientDto dto) {
        // 매장 재고 엔티티 조회
        StoreIngredient entity = storeIngredientRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 매장 재고 ID: " + dto.getId()));

        // 필요한 필드만 수정 (수량, 단가, 최소주문단위, 유통기한, 입고날짜 등)
        entity.setQuantity(dto.getQuantity());
        entity.setUnitCost(dto.getUnitCost());
        entity.setMinimumOrderUnit(dto.getMinimumOrderUnit());
        entity.setExpiredDate(dto.getExpiredDate());
        entity.setReceivedDate(dto.getReceivedDate());

        // 저장
        storeIngredientRepository.save(entity);
    }


    //폐기목록 조회(본사)
    @Override
    public List<DisposalDto> searchHqDisposals(PageInfo pageInfo, Integer categoryId, String keyword, String startDateStr, String endDateStr) {
        LocalDate startDate = (startDateStr != null && !startDateStr.isBlank()) ? LocalDate.parse(startDateStr) : null;
        LocalDate endDate = (endDateStr != null && !endDateStr.isBlank()) ? LocalDate.parse(endDateStr) : null;
        
        // 본사 storeId (예: 1)
        Integer hqStoreId = 1;

        // 총 개수 조회
        int totalCount = hqInventoryDslRepository.countHqDisposals(hqStoreId, categoryId, keyword, startDate, endDate);
        pageInfo.setAllPage((int) Math.ceil((double) totalCount / 10));

        int block = 5;
        int curPage = pageInfo.getCurPage() == null ? 1 : pageInfo.getCurPage();
        int allPage = pageInfo.getAllPage() == null ? 1 : pageInfo.getAllPage();

        int startPage = ((curPage - 1) / block) * block + 1;
        int endPage = Math.min(startPage + block - 1, allPage);

        pageInfo.setStartPage(startPage);
        pageInfo.setEndPage(endPage);

        // 목록 조회
        List<Disposal> result = hqInventoryDslRepository.selectHqDisposalListByFiltersPaging(
            hqStoreId, categoryId, keyword, startDate, endDate,
            PageRequest.of(curPage - 1, 10)
        );

        return result.stream().map(Disposal::toDto).collect(Collectors.toList());
    }


    //폐기목록 조회(매장)
    @Override
    public List<DisposalDto> searchStoreDisposals(PageInfo pageInfo, Integer storeId, Integer categoryId, String keyword, String startDateStr, String endDateStr) {
        LocalDate startDate = (startDateStr != null && !startDateStr.isBlank()) ? LocalDate.parse(startDateStr) : null;
        LocalDate endDate = (endDateStr != null && !endDateStr.isBlank()) ? LocalDate.parse(endDateStr) : null;

        int totalCount = storeInventoryDslRepository.countStoreDisposals(storeId, categoryId, keyword, startDate, endDate);
        pageInfo.setAllPage((int) Math.ceil((double) totalCount / 10));
        int block = 5;
        int curPage = pageInfo.getCurPage() == null ? 1 : pageInfo.getCurPage();
        int allPage = pageInfo.getAllPage() == null ? 1 : pageInfo.getAllPage();

        int startPage = ((curPage - 1) / block) * block + 1;
        int endPage = Math.min(startPage + block - 1, allPage);

        pageInfo.setStartPage(startPage);
        pageInfo.setEndPage(endPage);

        List<Disposal> result = storeInventoryDslRepository.selectStoreDisposalListByFiltersPaging(
            storeId, categoryId, keyword, startDate, endDate,
            PageRequest.of(curPage - 1, 10)
        );

        return result.stream().map(Disposal::toDto).collect(Collectors.toList());
    }


    // 폐기신청 (본사/매장 모두)
    @Override
    @Transactional
    public void processDisposalRequest(List<DisposalDto> disposalList) {
        // 본사 Store(ID=1)
        Store hqStore = storeRepository.findByName("본사")
                .orElseThrow(() -> new IllegalArgumentException("본사 매장을 찾을 수 없습니다."));
        Integer hqStoreId = hqStore.getId();

        for (DisposalDto item : disposalList) {
            Integer storeId = item.getStoreId();
            // 본사 폐기신청
            if (storeId.equals(hqStoreId)) {
                HqIngredient hqIngredient = hqIngredientRepository.findById(item.getId())
                        .orElseThrow(() -> new IllegalArgumentException("재고 ID " + item.getId() + " 를 찾을 수 없습니다."));
                int currentQty = hqIngredient.getQuantity() == null ? 0 : hqIngredient.getQuantity();
                int disposalAmount = item.getQuantity();
                if (disposalAmount <= 0) throw new IllegalArgumentException("폐기량은 0보다 커야 합니다. 재고 ID: " + item.getId());
                if (disposalAmount > currentQty) throw new IllegalArgumentException("폐기량이 현재 재고량보다 많습니다. 재고 ID: " + item.getId());

                //hqIngredient.setQuantity(currentQty - disposalAmount);
                //hqIngredientRepository.save(hqIngredient);
                //재고 아예 삭제
                hqIngredientRepository.delete(hqIngredient);

                // 본사 재고라면 Disposal에 storeIngredientId는 null/불필요하거나 본사재고 id를 넣어도 됨
                Disposal disposal = Disposal.builder()
                        .ingredient(hqIngredient.getIngredient())
                        .store(hqStore)
                        .quantity(disposalAmount)
                        .status("완료")
                        .storeIngredientId(null) 
                        .requestedAt(LocalDate.now())
                        .memo("유통기한 초과로 폐기 처리(즉시완료)")
                        .build();
                disposalRepository.save(disposal);

                InventoryRecord record = InventoryRecord.builder()
                        .ingredient(hqIngredient.getIngredient())
                        .store(hqStore)
                        .quantity(disposalAmount)
                        .changeType("출고")
                        .memo("폐기")
                        .date(LocalDateTime.now())
                        .build();
                recordRepository.save(record);

            //매장 폐기신청
            } else {
                Store store = storeRepository.findById(storeId)
                        .orElseThrow(() -> new IllegalArgumentException("매장 ID " + storeId + " 를 찾을 수 없습니다."));
                StoreIngredient storeIngredient = storeIngredientRepository.findById(item.getId())
                        .orElseThrow(() -> new IllegalArgumentException("재고 ID " + item.getId() + " 를 찾을 수 없습니다."));
                int currentQty = storeIngredient.getQuantity() == null ? 0 : storeIngredient.getQuantity();
                int disposalAmount = item.getQuantity();
                if (disposalAmount <= 0) throw new IllegalArgumentException("폐기량은 0보다 커야 합니다. 재고 ID: " + item.getId());
                if (disposalAmount > currentQty) throw new IllegalArgumentException("폐기량이 현재 재고량보다 많습니다. 재고 ID: " + item.getId());

                storeIngredient.setQuantity(currentQty - disposalAmount);
                storeIngredientRepository.save(storeIngredient);

                // 반드시 어떤 StoreIngredient를 폐기하는지 Disposal에 storeIngredientId를 기록!!
                Disposal disposal = Disposal.builder()
                        .ingredient(storeIngredient.getIngredient())
                        .store(store)
                        .quantity(disposalAmount)
                        .status("신청")
                        .storeIngredientId(storeIngredient.getId()) 
                        .requestedAt(LocalDate.now())
                        .memo("유통기한 초과로 폐기 신청")
                        .build();
                disposalRepository.save(disposal);
            }
        }
    }

    // 폐기 승인
    @Override
    @Transactional
    public void approveDisposals(List<Integer> disposalIds) {
        for (Integer disposalId : disposalIds) {
            Disposal disposal = disposalRepository.findById(disposalId)
                .orElseThrow(() -> new IllegalArgumentException("폐기 ID " + disposalId + " 없음"));
            Store store = disposal.getStore();
            Ingredient ingredient = disposal.getIngredient();
            int disposalQty = disposal.getQuantity();

            // 본사 폐기 : 상태만 완료로
            if (store.getId() == 1) {
                disposal.setStatus("완료");
                disposal.setProcessedAt(LocalDate.now());
                disposalRepository.save(disposal);
                continue;
            }
            // 매장 폐기 : storeIngredientId로 해당 StoreIngredient 삭제!
            StoreIngredient storeIngredient = storeIngredientRepository.findById(disposal.getStoreIngredientId()).orElse(null);
            if (storeIngredient != null) {
                //storeIngredient.setQuantity(0);
                //storeIngredientRepository.save(storeIngredient);
                storeIngredientRepository.delete(storeIngredient);

            }
            // 출고 기록
            InventoryRecord record = InventoryRecord.builder()
                .ingredient(ingredient)
                .store(store)
                .quantity(disposalQty)
                .changeType("출고")
                .memo("폐기")
                .date(LocalDateTime.now())
                .build();
            recordRepository.save(record);

            disposal.setStatus("완료");
            disposal.setProcessedAt(LocalDate.now());
            disposalRepository.save(disposal);
        }
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

    //매장 가져오기(본사제외)
    @Override
    public List<StoreDto> getStoresExceptHQ() {
        return storeRepository.findAll().stream()
                .filter(store -> store.getId() != 1)  // 본사(storeId=1) 제외
                .map(Store::toDto)
                .collect(Collectors.toList());
    }
    
    //재료설정 조회(본사)
    @Override
    public List<StoreIngredientSettingDto> getHqSettingsByFilters(Integer storeId, Integer categoryId, String keyword, PageInfo pageInfo) {
        // curPage가 0 또는 음수면 1로 기본 설정
        if (pageInfo.getCurPage() <= 0) {
            pageInfo.setCurPage(1);
        }
        
        long totalCount = hqInventoryDslRepository.countHqSettingsByFilters(storeId, categoryId, keyword);

        int allPage = (int) Math.ceil((double) totalCount / PAGE_SIZE);
        int startPage = ((pageInfo.getCurPage() - 1) / 10) * 10 + 1;
        int endPage = Math.min(startPage + 9, allPage);

        pageInfo.setStartPage(startPage);
        pageInfo.setEndPage(endPage);
        pageInfo.setAllPage(allPage);

        int offset = (pageInfo.getCurPage() - 1) * PAGE_SIZE;

        return hqInventoryDslRepository.findHqSettingsByFilters(storeId, categoryId, keyword, offset, PAGE_SIZE);
    }

    //재료설정 조회(매장)
    @Override
    public List<StoreIngredientSettingDto> getStoreSettingsByFilters(Integer storeId, Integer categoryId, String keyword, PageInfo pageInfo) {
        if (pageInfo.getCurPage() <= 0) {
            pageInfo.setCurPage(1);
        }
        
        long totalCount = storeInventoryDslRepository.countStoreSettingsByFilters(storeId, categoryId, keyword);

        int allPage = (int) Math.ceil((double) totalCount / PAGE_SIZE);
        int startPage = ((pageInfo.getCurPage() - 1) / 10) * 10 + 1;
        int endPage = Math.min(startPage + 9, allPage);

        pageInfo.setStartPage(startPage);
        pageInfo.setEndPage(endPage);
        pageInfo.setAllPage(allPage);

        int offset = (pageInfo.getCurPage() - 1) * PAGE_SIZE;

        return storeInventoryDslRepository.findStoreSettingsByFilters(storeId, categoryId, keyword, offset, PAGE_SIZE);
    }

    // 수정
    @Transactional
    public void updateSetting(StoreIngredientSettingDto dto) {
        if (dto.getId() == null) throw new IllegalArgumentException("id는 필수입니다");
        StoreIngredientSetting entity = storeIngredientSettingRepository.findById(dto.getId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id: " + dto.getId()));

        entity.setMinQuantity(dto.getMinQuantity());
        entity.setMaxQuantity(dto.getMaxQuantity());

        storeIngredientSettingRepository.save(entity);
    }

    // 추가
    @Transactional
    public StoreIngredientSettingDto addSetting(StoreIngredientSettingDto dto) {
        Store store = storeRepository.findById(dto.getStoreId())
            .orElseThrow(() -> new IllegalArgumentException("매장 없음"));
        Ingredient ingredient = ingredientRepository.findById(dto.getIngredientId())
            .orElseThrow(() -> new IllegalArgumentException("재료 없음"));

        StoreIngredientSetting entity = StoreIngredientSetting.builder()
            .store(store)
            .ingredient(ingredient)
            .minQuantity(dto.getMinQuantity())
            .maxQuantity(dto.getMaxQuantity())
            .build();
        StoreIngredientSetting saved = storeIngredientSettingRepository.save(entity);
        return saved.toDto();
    }

    //전체 재료 조회
    @Override
    public List<IngredientDto> getAllIngredients() {
        List<Ingredient> ingredients = hqInventoryDslRepository.getAllIngredients();

        return ingredients.stream()
                .map(Ingredient::toDto)   
                .collect(Collectors.toList());
    }
    
    //재고기록 추가
    @Override
    public void addRecord(InventoryRecordDto dto) {
        Ingredient ingredient = ingredientRepository.findById(dto.getIngredientId())
                .orElseThrow(() -> new IllegalArgumentException("재료 ID 오류: " + dto.getIngredientId()));

        Store store = storeRepository.findById(dto.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("매장 ID 오류: " + dto.getStoreId()));

        InventoryRecord record = dto.toEntity(ingredient, store);
        recordRepository.save(record);
    }
    
    //재고 기록 조회
    @Override
    public List<InventoryRecordDto> getRecordsByStoreAndType(Integer storeId, String changeType, PageInfo pageInfo) {
        if (storeId == null) return List.of();

        PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, PAGE_SIZE);

        if (storeId == 1) {
            // 본사 인벤토리 기록 조회
            return hqInventoryDslRepository.findHqInventoryRecords(changeType, pageRequest);
        } else {
            // 매장 인벤토리 기록 조회
            return storeInventoryDslRepository.findStoreInventoryRecords(storeId, changeType, pageRequest);
        }
    }
    
    //카테고리 추가
    @Override
    @Transactional
    public Integer addCategory(String name) {
        IngredientCategory cat = categoryRepository.findByName(name).orElse(null);
        if (cat == null) {
            cat = new IngredientCategory();   
            cat.setName(name);                
            cat = categoryRepository.save(cat);
        }
        return cat.getId();
    }

    //재료추가
    @Override
    @Transactional
    public Integer addIngredient(String name, Integer categoryId, String unit) {
        Ingredient ingredient = ingredientRepository.findByNameAndCategoryId(name, categoryId).orElse(null);
        if (ingredient == null) {
            IngredientCategory cat = categoryRepository.findById(categoryId).orElseThrow();
            ingredient = new Ingredient();    
            ingredient.setName(name);
            ingredient.setCategory(cat);
            ingredient.setUnit(unit);
            ingredient = ingredientRepository.save(ingredient);
        }
        return ingredient.getId();
    }
}