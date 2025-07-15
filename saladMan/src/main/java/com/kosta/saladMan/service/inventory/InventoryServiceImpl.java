package com.kosta.saladMan.service.inventory;

import com.kosta.saladMan.dto.dashboard.DisposalSummaryDto;
import com.kosta.saladMan.dto.dashboard.InventoryExpireSummaryDto;
import com.kosta.saladMan.dto.dashboard.MainStockSummaryDto;
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
import java.util.ArrayList;
import java.util.HashMap;
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

	// 본사 재고조회
	@Override
	public List<HqIngredientDto> getHqInventory(Integer storeId, Integer categoryId, String keyword,
			String startDateStr, String endDateStr, PageInfo pageInfo, String sortOption) {
		LocalDate startDate = (startDateStr == null || startDateStr.isBlank()) ? null : LocalDate.parse(startDateStr);
		LocalDate endDate = (endDateStr == null || endDateStr.isBlank()) ? null : LocalDate.parse(endDateStr);

		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, PAGE_SIZE);
		long totalCount = hqInventoryDslRepository.countHqInventoryByFilters(categoryId, keyword, startDate, endDate);
		pageInfo.setAllPage((int) Math.ceil((double) totalCount / PAGE_SIZE));

		int start = (pageInfo.getCurPage() - 1) / 5 * 5 + 1;
		int end = Math.min(start + 4, pageInfo.getAllPage());
		pageInfo.setStartPage(start);
		pageInfo.setEndPage(end);

		// 정렬 옵션 전달
		List<HqIngredientDto> list = hqInventoryDslRepository.findHqInventoryByFilters(categoryId, keyword, startDate,
				endDate, pageRequest, sortOption);

		Store hqStore = storeRepository.findById(1)
				.orElseThrow(() -> new IllegalArgumentException("본사 매장을 찾을 수 없습니다."));
		String hqStoreName = hqStore.getName();

		list.forEach(dto -> dto.setStoreName(hqStoreName));

		return list;
	}

	// 본사 유통기한 목록 조회
	@Override
	public List<HqIngredientDto> getHqInventoryExpiration(Integer storeId, Integer categoryId, String keyword,
			String startDateStr, String endDateStr, PageInfo pageInfo, String sortOption) {
		LocalDate startDate = (startDateStr == null || startDateStr.isBlank()) ? null : LocalDate.parse(startDateStr);
		LocalDate endDate = (endDateStr == null || endDateStr.isBlank()) ? null : LocalDate.parse(endDateStr);

		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, PAGE_SIZE);
		long totalCount = hqInventoryDslRepository.countHqInventoryExpirationByFilters(categoryId, keyword, startDate,
				endDate);
		pageInfo.setAllPage((int) Math.ceil((double) totalCount / PAGE_SIZE));

		int start = (pageInfo.getCurPage() - 1) / 5 * 5 + 1;
		int end = Math.min(start + 4, pageInfo.getAllPage());
		pageInfo.setStartPage(start);
		pageInfo.setEndPage(end);

		// 정렬 옵션 전달
		List<HqIngredientDto> list = hqInventoryDslRepository.findHqInventoryExpirationByFilters(categoryId, keyword,
				startDate, endDate, pageRequest, sortOption);

		Store hqStore = storeRepository.findById(1)
				.orElseThrow(() -> new IllegalArgumentException("본사 매장을 찾을 수 없습니다."));
		String hqStoreName = hqStore.getName();

		list.forEach(dto -> dto.setStoreName(hqStoreName));

		return list;
	}

	// 매장 재고조회
	@Override
	public List<StoreIngredientDto> getStoreInventory(Integer storeId, Integer categoryId, String keyword,
			String startDateStr, String endDateStr, PageInfo pageInfo, String sortOption) {
		if (storeId == null || storeId == 1)
			return List.of();

		LocalDate startDate = (startDateStr == null || startDateStr.isBlank()) ? null : LocalDate.parse(startDateStr);
		LocalDate endDate = (endDateStr == null || endDateStr.isBlank()) ? null : LocalDate.parse(endDateStr);

		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, PAGE_SIZE);
		Store storeEntity = storeRepository.findById(storeId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 매장 ID: " + storeId));

		long totalCount = storeInventoryDslRepository.countStoreInventoryByFilters(storeEntity.getId(), categoryId,
				keyword, startDate, endDate);
		pageInfo.setAllPage((int) Math.ceil((double) totalCount / PAGE_SIZE));
		int start = (pageInfo.getCurPage() - 1) / 5 * 5 + 1;
		int end = Math.min(start + 4, pageInfo.getAllPage());
		pageInfo.setStartPage(start);
		pageInfo.setEndPage(end);

		return storeInventoryDslRepository.findStoreInventoryByFilters(storeEntity.getId(), categoryId, keyword,
				startDate, endDate, pageRequest, sortOption);
	}

	// 매장 유통기한 목록 조회
	@Override
	public List<StoreIngredientDto> getStoreInventoryExpiration(Integer storeId, Integer categoryId, String keyword,
			String startDateStr, String endDateStr, PageInfo pageInfo, String sortOption) {
		if (storeId == null || storeId == 1)
			return List.of();

		LocalDate startDate = (startDateStr == null || startDateStr.isBlank()) ? null : LocalDate.parse(startDateStr);
		LocalDate endDate = (endDateStr == null || endDateStr.isBlank()) ? null : LocalDate.parse(endDateStr);

		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, PAGE_SIZE);
		Store storeEntity = storeRepository.findById(storeId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 매장 ID: " + storeId));

		long totalCount = storeInventoryDslRepository.countStoreInventoryExpirationByFilters(storeEntity.getId(),
				categoryId, keyword, startDate, endDate);
		pageInfo.setAllPage((int) Math.ceil((double) totalCount / PAGE_SIZE));
		int start = (pageInfo.getCurPage() - 1) / 5 * 5 + 1;
		int end = Math.min(start + 4, pageInfo.getAllPage());
		pageInfo.setStartPage(start);
		pageInfo.setEndPage(end);

		return storeInventoryDslRepository.findStoreInventoryExpirationByFilters(storeEntity.getId(), categoryId,
				keyword, startDate, endDate, pageRequest, sortOption);
	}

	@Override
	public List<StoreIngredientDto> getAllStoreInventory(Integer categoryId, String keyword, String startDateStr,
			String endDateStr, PageInfo pageInfo, String sortOption) {
		LocalDate startDate = (startDateStr == null || startDateStr.isBlank()) ? null : LocalDate.parse(startDateStr);
		LocalDate endDate = (endDateStr == null || endDateStr.isBlank()) ? null : LocalDate.parse(endDateStr);

		long totalCount = storeInventoryDslRepository.countStoreInventoryByFilters(null, categoryId, keyword, startDate,
				endDate);
		pageInfo.setAllPage((int) Math.ceil((double) totalCount / PAGE_SIZE));
		int start = (pageInfo.getCurPage() - 1) / 5 * 5 + 1;
		int end = Math.min(start + 4, pageInfo.getAllPage());
		pageInfo.setStartPage(start);
		pageInfo.setEndPage(end);

		return storeInventoryDslRepository.findStoreInventoryByFilters(null, categoryId, keyword, startDate, endDate,
				PageRequest.of(pageInfo.getCurPage() - 1, PAGE_SIZE), sortOption);
	}

	// 유통기한 목록 전체 매장
	@Override
	public List<StoreIngredientDto> getAllStoreInventoryExpiration(Integer categoryId, String keyword,
			String startDateStr, String endDateStr, PageInfo pageInfo, String sortOption) {
		LocalDate startDate = (startDateStr == null || startDateStr.isBlank()) ? null : LocalDate.parse(startDateStr);
		LocalDate endDate = (endDateStr == null || endDateStr.isBlank()) ? null : LocalDate.parse(endDateStr);

		long totalCount = storeInventoryDslRepository.countStoreInventoryExpirationByFilters(null, categoryId, keyword,
				startDate, endDate);
		pageInfo.setAllPage((int) Math.ceil((double) totalCount / PAGE_SIZE));
		int start = (pageInfo.getCurPage() - 1) / 5 * 5 + 1;
		int end = Math.min(start + 4, pageInfo.getAllPage());
		pageInfo.setStartPage(start);
		pageInfo.setEndPage(end);

		return storeInventoryDslRepository.findStoreInventoryExpirationByFilters(null, categoryId, keyword, startDate,
				endDate, PageRequest.of(pageInfo.getCurPage() - 1, PAGE_SIZE), sortOption);
	}

	// 재고 추가
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
		InventoryRecord record = InventoryRecord.builder().ingredient(ingredient).store(store)
				.quantity(dto.getQuantity()).changeType("입고").memo("신규 입고").date(LocalDateTime.now()).build();
		recordRepository.save(record);
	}

	// 본사 재고 수정
	@Override
	@Transactional
	public void updateHqIngredient(HqIngredientDto dto) {
		hqInventoryDslRepository.updateHqIngredient(dto);
	}

	// 매장 재고 수정
	@Override
	@Transactional
	public void updateStoreIngredient(StoreIngredientDto dto) {
		// 매장 재고 엔티티 조회
		StoreIngredient entity = storeIngredientRepository.findById(dto.getId())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 매장 재고 ID: " + dto.getId()));

		// 필드 수정 (카테고리, 재료명, 수량, 단가, 최소주문단위, 유통기한, 입고날짜 등)

		if (dto.getCategoryId() != null) {
			IngredientCategory category = categoryRepository.findById(dto.getCategoryId())
					.orElseThrow(() -> new IllegalArgumentException("카테고리 없음"));
			entity.setCategory(category);
		}
		if (dto.getIngredientId() != null) {
			Ingredient ingredient = ingredientRepository.findById(dto.getIngredientId())
					.orElseThrow(() -> new IllegalArgumentException("재료 없음"));
			entity.setIngredient(ingredient);
		}
		entity.setQuantity(dto.getQuantity());
		entity.setUnitCost(dto.getUnitCost());
		entity.setMinimumOrderUnit(dto.getMinimumOrderUnit());
		entity.setExpiredDate(dto.getExpiredDate());
		entity.setReceivedDate(dto.getReceivedDate());

		// 저장
		storeIngredientRepository.save(entity);
	}

	// 폐기목록 조회(본사)
	@Override
	public List<DisposalDto> searchHqDisposals(PageInfo pageInfo, Integer categoryId, String status,
			String startDateStr, String endDateStr, String sortOption, String keyword) {
		LocalDate startDate = (startDateStr != null && !startDateStr.isBlank()) ? LocalDate.parse(startDateStr) : null;
		LocalDate endDate = (endDateStr != null && !endDateStr.isBlank()) ? LocalDate.parse(endDateStr) : null;

		Integer hqStoreId = 1;

		// 총 개수 조회 (keyword 포함)
		int totalCount = hqInventoryDslRepository.countHqDisposals(hqStoreId, categoryId, status, startDate, endDate,
				keyword);
		pageInfo.setAllPage((int) Math.ceil((double) totalCount / PAGE_SIZE));

		int block = 5;
		int curPage = pageInfo.getCurPage() == null ? 1 : pageInfo.getCurPage();
		int allPage = pageInfo.getAllPage() == null ? 1 : pageInfo.getAllPage();
		int startPage = ((curPage - 1) / block) * block + 1;
		int endPage = Math.min(startPage + block - 1, allPage);
		pageInfo.setStartPage(startPage);
		pageInfo.setEndPage(endPage);

		int offset = (curPage - 1) * PAGE_SIZE;

		List<Disposal> result = hqInventoryDslRepository.selectHqDisposalListByFiltersPaging(hqStoreId, categoryId,
				status, startDate, endDate, offset, PAGE_SIZE, sortOption, keyword);

		return result.stream().map(Disposal::toDto).collect(Collectors.toList());
	}

	@Override
	public List<DisposalDto> searchStoreDisposals(PageInfo pageInfo, Integer storeId, Integer categoryId, String status,
			String startDateStr, String endDateStr, String sortOption, String keyword) {
		LocalDate startDate = (startDateStr != null && !startDateStr.isBlank()) ? LocalDate.parse(startDateStr) : null;
		LocalDate endDate = (endDateStr != null && !endDateStr.isBlank()) ? LocalDate.parse(endDateStr) : null;

		int totalCount = storeInventoryDslRepository.countStoreDisposals(storeId, categoryId, status, startDate,
				endDate, keyword);
		pageInfo.setAllPage((int) Math.ceil((double) totalCount / PAGE_SIZE));

		int block = 5;
		int curPage = pageInfo.getCurPage() == null ? 1 : pageInfo.getCurPage();
		int allPage = pageInfo.getAllPage() == null ? 1 : pageInfo.getAllPage();
		int startPage = ((curPage - 1) / block) * block + 1;
		int endPage = Math.min(startPage + block - 1, allPage);
		pageInfo.setStartPage(startPage);
		pageInfo.setEndPage(endPage);

		int offset = (curPage - 1) * PAGE_SIZE;

		List<Disposal> result = storeInventoryDslRepository.selectStoreDisposalListByFiltersPaging(storeId, categoryId,
				status, startDate, endDate, offset, PAGE_SIZE, sortOption, keyword);

		return result.stream().map(Disposal::toDto).collect(Collectors.toList());
	}

	// 모든 매장의 폐기목록
	@Override
	public List<DisposalDto> searchAllStoresExceptHqDisposals(PageInfo pageInfo, Integer categoryId, String status,
			String startDateStr, String endDateStr, String sortOption, String keyword) {
		LocalDate startDate = (startDateStr != null && !startDateStr.isBlank()) ? LocalDate.parse(startDateStr) : null;
		LocalDate endDate = (endDateStr != null && !endDateStr.isBlank()) ? LocalDate.parse(endDateStr) : null;

		// 본사 제외 매장 id 리스트 구하기
		List<Integer> storeIds = storeRepository.findAll().stream().map(Store::getId).filter(id -> id != 1) // 본사 제외
				.collect(Collectors.toList());

		int totalCount = storeInventoryDslRepository.countStoreDisposalsForStores(storeIds, categoryId, status,
				startDate, endDate, keyword);
		pageInfo.setAllPage((int) Math.ceil((double) totalCount / PAGE_SIZE));

		int block = 5;
		int curPage = pageInfo.getCurPage() == null ? 1 : pageInfo.getCurPage();
		int allPage = pageInfo.getAllPage() == null ? 1 : pageInfo.getAllPage();
		int startPage = ((curPage - 1) / block) * block + 1;
		int endPage = Math.min(startPage + block - 1, allPage);
		pageInfo.setStartPage(startPage);
		pageInfo.setEndPage(endPage);

		int offset = (curPage - 1) * PAGE_SIZE;

		List<Disposal> result = storeInventoryDslRepository.selectStoreDisposalListByFiltersPagingForStores(storeIds,
				categoryId, status, startDate, endDate, offset, PAGE_SIZE, sortOption, keyword);

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
				if (disposalAmount <= 0)
					throw new IllegalArgumentException("폐기량은 0보다 커야 합니다. 재고 ID: " + item.getId());
				if (disposalAmount > currentQty)
					throw new IllegalArgumentException("폐기량이 현재 재고량보다 많습니다. 재고 ID: " + item.getId());

				// hqIngredient.setQuantity(currentQty - disposalAmount);
				// hqIngredientRepository.save(hqIngredient);
				// 재고 아예 삭제
				hqIngredientRepository.delete(hqIngredient);

				// 본사 재고라면 Disposal에 storeIngredientId는 null/불필요하거나 본사재고 id를 넣어도 됨
				Disposal disposal = Disposal.builder().ingredient(hqIngredient.getIngredient()).store(hqStore)
						.quantity(disposalAmount).status("완료").storeIngredientId(hqIngredient.getId())
						.requestedAt(LocalDate.now())
						.memo(item.getMemo() != null && !item.getMemo().isBlank() ? item.getMemo()
								: "유통기한 초과로 폐기 처리(즉시완료)")
						.build();
				disposalRepository.save(disposal);
				// 폐기 기록 추가
				InventoryRecord record = InventoryRecord.builder().ingredient(hqIngredient.getIngredient())
						.store(hqStore).quantity(disposalAmount).changeType("폐기").memo("폐기").date(LocalDateTime.now())
						.build();
				recordRepository.save(record);

				// 매장 폐기신청
			} else {
				Store store = storeRepository.findById(storeId)
						.orElseThrow(() -> new IllegalArgumentException("매장 ID " + storeId + " 를 찾을 수 없습니다."));
				StoreIngredient storeIngredient = storeIngredientRepository.findById(item.getId())
						.orElseThrow(() -> new IllegalArgumentException("재고 ID " + item.getId() + " 를 찾을 수 없습니다."));
				int currentQty = storeIngredient.getQuantity() == null ? 0 : storeIngredient.getQuantity();
				int disposalAmount = item.getQuantity();
				if (disposalAmount <= 0)
					throw new IllegalArgumentException("폐기량은 0보다 커야 합니다. 재고 ID: " + item.getId());
				if (disposalAmount > currentQty)
					throw new IllegalArgumentException("폐기량이 현재 재고량보다 많습니다. 재고 ID: " + item.getId());

				storeIngredient.setQuantity(currentQty - disposalAmount);
				storeIngredientRepository.save(storeIngredient);

				// 반드시 어떤 StoreIngredient를 폐기하는지 Disposal에 storeIngredientId를 기록!!
				Disposal disposal = Disposal.builder().ingredient(storeIngredient.getIngredient()).store(store)
						.quantity(disposalAmount).status("대기").storeIngredientId(storeIngredient.getId())
						.requestedAt(LocalDate.now())
						.memo(item.getMemo() != null && !item.getMemo().isBlank() ? item.getMemo() : "유통기한 초과로 폐기 신청")
						.build();
				disposalRepository.save(disposal);

				System.out.println(item.getMemo());
			}
		}
	}

	// 폐기 승인
	@Override
	@Transactional
	public void approveDisposals(List<Integer> disposalIds) {
		System.out.println("==== approveDisposals 진입, disposalIds = " + disposalIds);

		for (Integer disposalId : disposalIds) {
			System.out.println("처리중 disposalId = " + disposalId);

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
			StoreIngredient storeIngredient = storeIngredientRepository.findById(disposal.getStoreIngredientId())
					.orElse(null);
			if (storeIngredient != null) {
				// storeIngredient.setQuantity(0);
				// storeIngredientRepository.save(storeIngredient);
				storeIngredientRepository.delete(storeIngredient);

			}
			// 출고 기록
			InventoryRecord record = InventoryRecord.builder().ingredient(ingredient).store(store).quantity(disposalQty)
					.changeType("출고").memo("폐기").date(LocalDateTime.now()).build();
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

	// 매장 가져오기
	@Override
	public List<StoreDto> getAllStores() {
		return storeRepository.findAll().stream().map(Store::toDto).collect(Collectors.toList());
	}

	// 매장 가져오기(본사제외)
	@Override
	public List<StoreDto> getStoresExceptHQ() {
		return storeRepository.findAll().stream().filter(store -> store.getId() != 1) // 본사(storeId=1) 제외
				.map(Store::toDto).collect(Collectors.toList());
	}

	// 재료설정 조회(본사)
	@Override
	public List<StoreIngredientSettingDto> getHqSettingsByFilters(Integer storeId, Integer categoryId, String keyword,
			PageInfo pageInfo) {
		// curPage가 0 또는 음수면 1로 기본 설정
		if (pageInfo.getCurPage() <= 0) {
			pageInfo.setCurPage(1);
		}

		long totalCount = hqInventoryDslRepository.countHqSettingsByFilters(storeId, categoryId, keyword);

		int allPage = (int) Math.ceil((double) totalCount / PAGE_SIZE);
		int startPage = ((pageInfo.getCurPage() - 1) / 5) * 5 + 1;
		int endPage = Math.min(startPage + 4, allPage);

		pageInfo.setStartPage(startPage);
		pageInfo.setEndPage(endPage);
		pageInfo.setAllPage(allPage);

		int offset = (pageInfo.getCurPage() - 1) * PAGE_SIZE;

		return hqInventoryDslRepository.findHqSettingsByFilters(storeId, categoryId, keyword, offset, PAGE_SIZE);
	}

	// 재료설정 조회(매장)
	@Override
	public List<StoreIngredientSettingDto> getStoreSettingsByFilters(Integer storeId, Integer categoryId,
			String keyword, PageInfo pageInfo) {
		if (pageInfo.getCurPage() <= 0) {
			pageInfo.setCurPage(1);
		}

		long totalCount = storeInventoryDslRepository.countStoreSettingsByFilters(storeId, categoryId, keyword);

		int allPage = (int) Math.ceil((double) totalCount / PAGE_SIZE);
		int startPage = ((pageInfo.getCurPage() - 1) / 5) * 5 + 1;
		int endPage = Math.min(startPage + 4, allPage);

		pageInfo.setStartPage(startPage);
		pageInfo.setEndPage(endPage);
		pageInfo.setAllPage(allPage);

		int offset = (pageInfo.getCurPage() - 1) * PAGE_SIZE;

		return storeInventoryDslRepository.findStoreSettingsByFilters(storeId, categoryId, keyword, offset, PAGE_SIZE);
	}

	// 재료설정 수정
	@Transactional
	public void updateSetting(StoreIngredientSettingDto dto) {
		if (dto.getId() == null)
			throw new IllegalArgumentException("id는 필수입니다");
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
		// 이미 등록 체크 등은 ingredientId/storeId로만 확인
		boolean exists = storeIngredientSettingRepository.existsByStoreIdAndIngredientId(dto.getStoreId(),
				dto.getIngredientId());
		if (exists)
			throw new IllegalArgumentException("이미 등록된 재료입니다.");

		StoreIngredientSetting entity = StoreIngredientSetting.builder().store(store).ingredient(ingredient)
				.minQuantity(dto.getMinQuantity()).maxQuantity(dto.getMaxQuantity()).build();
		StoreIngredientSetting saved = storeIngredientSettingRepository.save(entity);
		return saved.toDto();
	}

	// 전체 재료 조회
	@Override
	public List<IngredientDto> getAllIngredients() {
		List<Ingredient> ingredients = hqInventoryDslRepository.getAllIngredients();

		return ingredients.stream().map(Ingredient::toDto).collect(Collectors.toList());
	}

	// 재고기록 추가
	@Override
	public void addRecord(InventoryRecordDto dto) {
		Ingredient ingredient = ingredientRepository.findById(dto.getIngredientId())
				.orElseThrow(() -> new IllegalArgumentException("재료 ID 오류: " + dto.getIngredientId()));

		Store store = storeRepository.findById(dto.getStoreId())
				.orElseThrow(() -> new IllegalArgumentException("매장 ID 오류: " + dto.getStoreId()));

		InventoryRecord record = dto.toEntity(ingredient, store);
		recordRepository.save(record);
	}

	// 재고 기록 조회
	@Override
	public List<InventoryRecordDto> getRecordsByStoreAndType(Integer storeId, String changeType, PageInfo pageInfo) {
		if (storeId == null)
			return List.of();

		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, PAGE_SIZE);

		if (storeId == 1) {
			// 본사 인벤토리 기록 조회
			return hqInventoryDslRepository.findHqInventoryRecords(changeType, pageRequest);
		} else {
			// 매장 인벤토리 기록 조회
			return storeInventoryDslRepository.findStoreInventoryRecords(storeId, changeType, pageRequest);
		}
	}

	// 카테고리 조회
	@Override
	public List<IngredientCategoryDto> getAllCategories() {
		return categoryRepository.findAll().stream().map(IngredientCategory::toDto).collect(Collectors.toList());
	}

	// 카테고리 추가
	@Override
	@Transactional
	public IngredientCategoryDto addCategory(String name) {
		// 중복 체크
		IngredientCategory cat = categoryRepository.findByName(name).orElseGet(() -> {
			IngredientCategory e = new IngredientCategory();
			e.setName(name);
			return categoryRepository.save(e);
		});
		return cat.toDto();
	}

	@Override
	@Transactional
	public void updateCategory(Integer id, String name) {
		IngredientCategory cat = categoryRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("카테고리 없음: " + id));
		cat.setName(name);
		categoryRepository.save(cat);
	}

	@Override
	@Transactional
	public void deleteCategory(Integer categoryId) {
		IngredientCategory cat = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new IllegalArgumentException("카테고리 없음: " + categoryId));

		// (1) 해당 카테고리의 재료 먼저 삭제
		ingredientRepository.deleteByCategoryId(categoryId);

		// (2) 카테고리 삭제
		categoryRepository.delete(cat);
	}

	// 재료추가
	@Override
	@Transactional
	public IngredientDto addIngredient(String name, Integer categoryId, String unit) {
		IngredientCategory cat = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new IllegalArgumentException("카테고리 없음: " + categoryId));
		Ingredient ing = ingredientRepository.findByNameAndCategoryId(name, categoryId).orElseGet(() -> {
			Ingredient e = new Ingredient();
			e.setName(name);
			e.setUnit(unit);
			e.setCategory(cat);
			e.setAvailable(true);
			return ingredientRepository.save(e);
		});
		return ing.toDto();
	}

	@Override
	@Transactional
	public void updateIngredient(Integer id, String name, String unit) {
		Ingredient ing = ingredientRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("재료 없음: " + id));
		ing.setName(name);
		ing.setUnit(unit);
		ingredientRepository.save(ing);
	}

	@Override
	@Transactional
	public void deleteIngredient(Integer ingredientId) {
		Ingredient ing = ingredientRepository.findById(ingredientId)
				.orElseThrow(() -> new IllegalArgumentException("재료 없음: " + ingredientId));
		ingredientRepository.delete(ing);
	}

	// 재료설정 삭제
	@Override
	@Transactional
	public void deleteSetting(Integer id) {
		StoreIngredientSetting setting = storeIngredientSettingRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("설정 ID 없음: " + id));
		storeIngredientSettingRepository.delete(setting);
	}

	// 매장 재고 추가
	@Override
	public void addStoreIngredient(StoreIngredientDto dto) {
		// 분류(카테고리) 조회
		IngredientCategory category = categoryRepository.findById(dto.getCategoryId())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 ID: " + dto.getCategoryId()));
		// 재료 조회
		Ingredient ingredient = ingredientRepository.findById(dto.getIngredientId())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 재료 ID: " + dto.getIngredientId()));
		// 매장 조회
		Store store = storeRepository.findById(dto.getStoreId())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 매장 ID: " + dto.getStoreId()));

		StoreIngredient entity = dto.toEntity();
		entity.setStore(store);
		entity.setIngredient(ingredient);
		entity.setCategory(category);

		// 재고 저장
		storeIngredientRepository.save(entity);

		// 입고기록 추가
		InventoryRecord record = InventoryRecord.builder().ingredient(ingredient).store(store)
				.quantity(dto.getQuantity()).changeType("입고").memo("매장 신규 입고").date(LocalDateTime.now()).build();
		recordRepository.save(record);
	}

	// 임박재고 Top3+전체건수
	@Override
	public InventoryExpireSummaryDto getExpireSummaryTop3WithCountMerged(String startDate, String endDate) {
		return storeInventoryDslRepository.findExpireSummaryTop3WithCountMerged(startDate, endDate);
	}

	// 폐기 Top3+전체건수
	@Override
	public DisposalSummaryDto getDisposalSummaryTop3WithCountMerged(String startDate, String endDate) {
		return hqInventoryDslRepository.findDisposalSummaryTop3WithCountMerged(startDate, endDate);
	}

	// 재고 부족
	@Override
	public int getLowStockCount() {
		Integer hqStoreId = 1;
		// 재고 전체를 불러오고 minQuantity 이하 필터
		List<HqIngredientDto> all = getHqInventory(hqStoreId, null, null, null, null, new PageInfo(1), null);
		int cnt = 0;
		for (HqIngredientDto i : all) {
			// quantity가 null이 아니고, minQuantity도 null이 아니고, quantity <= minQuantity면 재고부족으로
			// 집계
			if (i.getQuantity() != null && i.getMinquantity() != null && i.getQuantity() <= i.getMinquantity()) {
				cnt++;
			}
		}
		return cnt;
	}

	@Override
	public InventoryExpireSummaryDto getStoreExpireSummary(Integer storeId, String startDate, String endDate) {
		return storeInventoryDslRepository.findExpireSummaryTop3WithCountMergedByStore(storeId, startDate, endDate);
	}

	@Override
	public List<MainStockSummaryDto> getMainStocksByPeriod(Integer storeId, LocalDate start, LocalDate end) {
		return storeInventoryDslRepository.findMainStocksByStoreForPeriod(storeId, start, end);
	}

	@Override
	public int getAutoOrderExpectedCount(Integer storeId) {
		return storeInventoryDslRepository.countAutoOrderExpectedByStore(storeId);
	}

	public int getStoreLowStockCount(Integer storeId) {
		// 해당 매장의 전체 재고 중, minQuantity 이하(또는 자체 로직 적용) 품목 수 반환
		return storeInventoryDslRepository.countLowStockByStore(storeId);
	}

	@Override
	public Map<String, Integer> getDisposalStatusCountByStore(Integer storeId) {
		// 1년 전부터 현재까지의 범위 설정 (LocalDateTime에서 LocalDate로 변환)
		LocalDate start = LocalDate.now().minusYears(1); // 1년 전 날짜 (시각 제외)
		LocalDate end = LocalDate.now(); // 현재 날짜 (시각 제외)

		// 결과 맵 생성
		Map<String, Integer> result = new HashMap<>();
		result.put("대기", disposalRepository.countByStoreIdAndStatusAndRequestedAtBetween(storeId, "대기", start, end));
		result.put("완료", disposalRepository.countByStoreIdAndStatusAndRequestedAtBetween(storeId, "완료", start, end));
		result.put("반려", disposalRepository.countByStoreIdAndStatusAndRequestedAtBetween(storeId, "반려", start, end));

		return result;
	}
	
	
	// HQ의 부족 품목 리스트 반환
	@Override
	public List<HqIngredientDto> getLowStockList() {
	    Integer hqStoreId = 1;
	    List<HqIngredientDto> all = getHqInventory(hqStoreId, null, null, null, null, new PageInfo(1), null);
	    List<HqIngredientDto> result = new ArrayList<>();
	    for (HqIngredientDto i : all) {
	        if (i.getQuantity() != null && i.getMinquantity() != null && i.getQuantity() <= i.getMinquantity()) {
	            result.add(i);
	        }
	    }
	    return result;
	}
	

}