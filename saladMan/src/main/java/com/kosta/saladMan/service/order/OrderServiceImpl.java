package com.kosta.saladMan.service.order;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.kosta.saladMan.dto.inventory.HqIngredientDto;
import com.kosta.saladMan.dto.inventory.IngredientItemDto;
import com.kosta.saladMan.dto.purchaseOrder.FixedOrderItemDto;
import com.kosta.saladMan.dto.purchaseOrder.LowStockItemDto;
import com.kosta.saladMan.dto.purchaseOrder.PurchaseOrderDto;
import com.kosta.saladMan.dto.purchaseOrder.PurchaseOrderItemDto;
import com.kosta.saladMan.dto.purchaseOrder.StoreOrderItemDto;
import com.kosta.saladMan.entity.inventory.HqIngredient;
import com.kosta.saladMan.entity.inventory.Ingredient;
import com.kosta.saladMan.entity.inventory.InventoryRecord;
import com.kosta.saladMan.entity.inventory.StoreIngredient;
import com.kosta.saladMan.entity.inventory.StoreIngredientStock;
import com.kosta.saladMan.entity.purchaseOrder.FixedOrderItem;
import com.kosta.saladMan.entity.purchaseOrder.FixedOrderTemplate;
import com.kosta.saladMan.entity.purchaseOrder.PurchaseOrder;
import com.kosta.saladMan.entity.purchaseOrder.PurchaseOrderItem;
import com.kosta.saladMan.entity.store.Store;
import com.kosta.saladMan.repository.StoreRepository;
import com.kosta.saladMan.repository.inventory.HqIngredientRepository;
import com.kosta.saladMan.repository.inventory.IngredientRepository;
import com.kosta.saladMan.repository.inventory.InventoryRecordRepository;
import com.kosta.saladMan.repository.inventory.StoreIngredientRepository;
import com.kosta.saladMan.repository.inventory.StoreIngredientStockRepository;
import com.kosta.saladMan.repository.order.FixedOrderDslRepository;
import com.kosta.saladMan.repository.order.FixedOrderItemRepository;
import com.kosta.saladMan.repository.order.FixedOrderTemplateRepository;
import com.kosta.saladMan.repository.order.IngredientDslRepository;
import com.kosta.saladMan.repository.order.PuchaseOrderDslRepository;
import com.kosta.saladMan.repository.order.PurchaseOrderItemRepository;
import com.kosta.saladMan.repository.order.PurchaseOrderRepository;
import com.kosta.saladMan.repository.order.StoreIngredientDslRepository;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private IngredientRepository ingredientRepository;

	@Autowired
	private IngredientDslRepository ingredientDslRepository;
	
	@Autowired
	private StoreRepository storeRepository;

	@Autowired
	private StoreIngredientDslRepository storeIngredientDslRepository;
	
	@Autowired
	private StoreIngredientRepository storeIngredientRepository;

	@Autowired
	private PurchaseOrderRepository purchaseOrderRepository;

	@Autowired
	private PurchaseOrderItemRepository purchaseOrderItemRepository;

	@Autowired
	private PuchaseOrderDslRepository purchaseOrderDslRepository;
	
	@Autowired
	private HqIngredientRepository hqIngredientRepository;
	
	@Autowired
	private StoreIngredientStockRepository storeIngredientStockRepository;
	
	@Autowired
	private InventoryRecordRepository inventoryRecordRepository;
	
	@Autowired
	private FixedOrderDslRepository fixedOrderDslRepository;
	
	@Autowired
	private FixedOrderTemplateRepository fixedOrderTemplateRepository;
	
	@Autowired
	private FixedOrderItemRepository fixedOrderItemRepository;

	// 재료 리스트
	@Override
	public Page<IngredientItemDto> getIngredientList(Boolean available, String category, String keyword, int page,
			int size) throws Exception {

		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
		return ingredientDslRepository.ingredientList(available, category, keyword, pageable);
	}

	// 발주가능여부 껏켯
	@Override
	public Boolean toggleIngredientAvailability(Integer id) throws Exception {

		Ingredient ingredient = ingredientRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("재료가 존재하지 않습니다."));

		ingredient.setAvailable(!ingredient.getAvailable()); // 반전
		ingredientRepository.save(ingredient);

		return ingredient.getAvailable(); // 변경된 상태 반환

	}
	
	//발주 신청 목록
	@Override
	public Map<String, Object> getOrderListByHq(String storeName, String status, LocalDate startDate,
			LocalDate endDate, Pageable pageable) throws Exception {
		Page<PurchaseOrderDto> resultPage = purchaseOrderDslRepository.findOrderApplyList(storeName, status,
				startDate, endDate, pageable);

		Map<String, Object> response = new HashMap<>();
		response.put("orders", resultPage.getContent());
		response.put("currentPage", resultPage.getNumber());
		response.put("totalPages", resultPage.getTotalPages());
		response.put("totalElements", resultPage.getTotalElements());
		response.put("pageSize", resultPage.getSize());
		return response;

	}
	
	//발주 신청 상세
	@Override
	public List<PurchaseOrderItemDto> getOrderDetailByHq(Integer purchaseOrderId) throws Exception {
		
		List<PurchaseOrderItemDto> itemList = purchaseOrderDslRepository.orderApplyDetail(purchaseOrderId);
		if(itemList.isEmpty()) return itemList;
		
		PurchaseOrder order = purchaseOrderRepository.findById(purchaseOrderId)
			    .orElseThrow(() -> new RuntimeException("해당 발주 없음"));
		
		//주문한 storeId
		Integer storeId =  order.getStore().getId();
		//배송일수
		Integer deliveryDay = order.getStore().getDeliveryDay();
		
		LocalDate today=LocalDate.now();
		LocalDate limitDate = today.plusDays(deliveryDay);
		
		for(PurchaseOrderItemDto item : itemList) {
			List<HqIngredientDto> stockList = purchaseOrderDslRepository.findAvailableStockByIngredientId(item.getIngredientId(),storeId,limitDate);
			System.out.println(item.getIngredientId());
			System.out.println(stockList);
			item.setStockList(stockList);
		}
		
		return itemList;
	}
	
	// 발주 신청 수락
	@Transactional
	@Override
	public void updateOrderItems(List<PurchaseOrderItemDto> items) throws Exception {
	    if (items == null || items.isEmpty()) return;

	    Integer orderId = items.get(0).getPurchaseOrderId();
	    List<PurchaseOrderItem> orderItems = purchaseOrderItemRepository.findByPurchaseOrderId(orderId);

	    Map<Integer, PurchaseOrderItemDto> dtoMap = items.stream()
	            .collect(Collectors.toMap(PurchaseOrderItemDto::getId, dto -> dto));

	    PurchaseOrder order = orderItems.get(0).getPurchaseOrder();

	    for (PurchaseOrderItem item : orderItems) {
	        PurchaseOrderItemDto dto = dtoMap.get(item.getId());
	        if (dto != null) {
	            item.setApprovalStatus(dto.getApprovalStatus());
	            item.setRejectionReason(dto.getRejectionReason());

	            if ("승인".equals(dto.getApprovalStatus())) {
	                List<Integer> selectedStockIds = dto.getSelectedStockIds();
	                if (selectedStockIds == null || selectedStockIds.isEmpty()) {
	                    throw new IllegalArgumentException("승인된 항목에 재고 선택이 없습니다.");
	                }

	                int requiredQty = item.getOrderedQuantity();
	                List<HqIngredient> stocks = hqIngredientRepository.findAllById(selectedStockIds);

	                int availableTotal = stocks.stream()
	                        .mapToInt(s -> s.getQuantity() != null ? s.getQuantity() : 0)
	                        .sum();

	                if (availableTotal < requiredQty) {
	                    throw new IllegalStateException(item.getIngredient().getName() + " 재고 수량이 부족합니다.");
	                }

	                int remain = requiredQty;
	                for (HqIngredient stock : stocks) {
	                    if (stock == null) continue;

	                    int stockQty = stock.getQuantity() != null ? stock.getQuantity() : 0;
	                    if (stockQty <= 0) continue;

	                    int deduct = Math.min(remain, stockQty);
	                    stock.setQuantity(stockQty - deduct);
	                    remain -= deduct;

	                    // ⬇ StoreIngredientStock 입고 처리
	                    StoreIngredientStock storeStock = StoreIngredientStock.builder()
	                        .store(order.getStore())
	                        .ingredient(stock.getIngredient())
	                        .quantity(deduct)
	                        .expiredDate(stock.getExpiredDate())
	                        .unitCost(stock.getUnitCost())
	                        .minimumOrderUnit(stock.getMinimumOrderUnit())
	                        .receivedDate(LocalDate.now())
	                        .purchaseOrder(order)
	                        .build();
	                    storeIngredientStockRepository.save(storeStock);

	                    if (remain <= 0) break;
	                }
	            }
	        }
	    }

	    long approved = orderItems.stream().filter(i -> "승인".equals(i.getApprovalStatus())).count();
	    long rejected = orderItems.stream().filter(i -> "반려".equals(i.getApprovalStatus())).count();

	    if (approved > 0 && rejected > 0) {
	        order.setOrderStatus("부분승인");
	    } else if (approved > 0) {
	        order.setOrderStatus("승인");
	    } else {
	        order.setOrderStatus("반려");
	    }

	    if (approved > 0) {
	        order.setStatus("입고완료");
	    } else {
	        order.setStatus("주문취소");
	    }
	}

	// -----------------매장-------------------------

	// 수량 미달 확인
	@Override
	public List<LowStockItemDto> getLowStockItems(Integer storeId) {

		return storeIngredientDslRepository.findLowStockIngredientsByStore(storeId);

	}

	@Override
	public List<StoreOrderItemDto> getOrderItems(Integer id, String category, String keyword) throws Exception {
		return storeIngredientDslRepository.findAvailableOrderItemsByStore(id, category, keyword);
	}

	// 발주 신청
	@Transactional
	@Override
	public void createOrder(Store storeInfo, List<StoreOrderItemDto> items,String purchaseType) throws Exception {
		int total = items.stream().mapToInt(item -> {
			Integer qty = item.getQuantity();
			Integer cost = item.getUnitCost();
			return (qty == null || cost == null) ? 0 : item.getTotalPrice();
		}).sum();
		PurchaseOrder order = PurchaseOrder.builder().store(storeInfo).orderDateTime(LocalDateTime.now()).status("대기중")
				.requestedBy(storeInfo.getName()).totalPrice(total).purType(purchaseType).build();
		purchaseOrderRepository.save(order);

		for (StoreOrderItemDto sOrderDto : items) {
			Ingredient ingredient = ingredientRepository.findById(sOrderDto.getIngredientId())
					.orElseThrow(() -> new Exception("존재하지 않는 재료"));

			PurchaseOrderItem orderItem = new PurchaseOrderItem();
			orderItem.setPurchaseOrder(order);
			orderItem.setIngredient(ingredient);
			orderItem.setOrderedQuantity(sOrderDto.getQuantity());
			orderItem.setReceivedQuantity(0);
			orderItem.setTotalPrice(sOrderDto.getTotalPrice());
			orderItem.setApprovalStatus("대기중");
			orderItem.setRejectionReason(null);

			purchaseOrderItemRepository.save(orderItem);

		}

	}

	// 발주 목록
	@Override
	public Page<PurchaseOrderDto> getPagedOrderList(Integer id, String orderType, String productName,
			LocalDate startDate, LocalDate endDate, int page, int size) throws Exception {

		Pageable pageable = PageRequest.of(page, size, Sort.by("orderDateTime").descending());

		return purchaseOrderDslRepository.findPagedOrders(id, orderType, productName, startDate, endDate, pageable);
	}
	
	
	//입고검수
	@Override
	public List<PurchaseOrderItemDto> getInspectionInfo(Integer orderId) throws Exception {

		return purchaseOrderDslRepository.getInspectionInfo(orderId);
	}
	
	//검수완료
	@Transactional
	@Override
	public void saveInspectionResults(List<PurchaseOrderItemDto> items) throws Exception {
	    if (items == null || items.isEmpty()) return;
	   
	    Integer orderId = items.get(0).getPurchaseOrderId();
	    PurchaseOrder order = purchaseOrderRepository.findById(orderId)
	            .orElseThrow(() -> new Exception("해당 발주가 존재하지 않습니다. id: " + orderId));

		for (PurchaseOrderItemDto dto : items) {
	        // 1. 발주 품목 조회
	        PurchaseOrderItem item = purchaseOrderItemRepository.findById(dto.getId())
	                .orElseThrow(() -> new Exception("해당 발주 품목이 존재하지 않습니다. id: " + dto.getId()));

	        // ✅ 2. 반려 항목은 스킵
	        if ("반려".equals(dto.getApprovalStatus())) {
	            continue; // 이 항목은 아무것도 하지 않음
	        }

	        
	        // 2. 검수 상태 및 메모 업데이트
	        item.setInspection(dto.getInspection());
	        item.setInspectionNote(dto.getInspectionNote());
	        item.setReceivedQuantity(dto.getOrderedQuantity()); // 실제 입고 수량 반영
	        purchaseOrderItemRepository.save(item);
	        
	        

	        // 3. StoreIngredient 업데이트 or 생성
	        Store store = item.getPurchaseOrder().getStore();
	        Ingredient ingredient = item.getIngredient();
	        int quantity = dto.getOrderedQuantity();

	        //유통기한 조회
	        Optional<StoreIngredientStock> stockOptional = storeIngredientStockRepository
	            .findFirstByPurchaseOrderIdAndIngredientId(orderId, ingredient.getId());

	        LocalDate expiredDate = stockOptional
	            .map(StoreIngredientStock::getExpiredDate)
	            .orElse(LocalDate.now().plusDays(5));
	        
	        LocalDate receivedDate = LocalDate.now(); // 오늘 날짜
	        
	        Integer minimumOrderUnit = stockOptional
	        	    .map(StoreIngredientStock::getMinimumOrderUnit)
	        	    .orElse(null);
	        
	        Integer stockQuantity = stockOptional
	        	    .map(StoreIngredientStock::getQuantity)
	        	    .orElse(0);
	        
	        StoreIngredient storeIngredient = StoreIngredient.builder()
	                        .store(store)
	                        .ingredient(ingredient)
	                        .category(ingredient.getCategory())
	                        .unitCost(dto.getUnitCost())
	                        .quantity(stockQuantity) // 검수된 수량만
	                        .expiredDate(expiredDate)
	                        .receivedDate(receivedDate)
	                        .minimumOrderUnit(minimumOrderUnit)
	                        .build();

	        storeIngredientRepository.save(storeIngredient);

	        // 4. InventoryRecord 기록 추가 (입고)
	        InventoryRecord record = InventoryRecord.builder()
	                .store(store)
	                .ingredient(ingredient)
	                .changeType("입고")
	                .date(LocalDateTime.now())
	                .quantity(quantity)
	                .memo("매장 입고")
	                .build();

	        inventoryRecordRepository.save(record);
	    }
		
	    // 5. 주문 상태 '검수 완료'로 변경
	    order.setStatus("검수완료");
	    purchaseOrderRepository.save(order);
	}
	
	//발주 설정 조회
	@Override
	public List<FixedOrderItemDto> getSettings(Integer id) throws Exception {
		return fixedOrderDslRepository.findAllByStore(id);
	}

	
	@Override
	@Transactional
	public void saveSettings(Integer id, List<FixedOrderItemDto> settingList) throws Exception {
		// FixedOrderTemplate 존재 여부 확인 or 생성
	    FixedOrderTemplate template = fixedOrderTemplateRepository.findByStoreId(id)
	        .orElseGet(() -> fixedOrderTemplateRepository.save(
	            FixedOrderTemplate.builder()
	                .store(storeRepository.getReferenceById(id))
	                .name("자동발주 설정")
	                .build()
	        ));
	    
	    Optional<FixedOrderTemplate> templateOpt = fixedOrderTemplateRepository.findByStoreId(id);
	    System.out.println("찾은 템플릿: " + templateOpt);

	    // 기존 FixedOrderItem 삭제
	    int deletedCount = fixedOrderItemRepository.deleteByFixedOrderTemplateId(template.getId());
	    System.out.println("삭제된 FixedOrderItem 행 수: " + deletedCount);
	    // 새로 저장
	    List<FixedOrderItem> newItems = settingList.stream()
	        .filter(dto -> dto.getAutoOrderEnabled() != null && dto.getAutoOrderEnabled())
	        .map(dto -> FixedOrderItem.builder()
	            .fixedOrderTemplate(template)
	            .ingredient(ingredientRepository.getReferenceById(dto.getIngredientId()))
	            .autoOrderEnabled(dto.getAutoOrderEnabled())
	            .autoOrderQty(dto.getAutoOrderQty())
	            .build())
	        .collect(Collectors.toList());

	    fixedOrderItemRepository.saveAll(newItems);		
	}

	@Override
	public Boolean getAutoOrderEnabled(Integer id) throws Exception {
		// TODO Auto-generated method stub
		return storeRepository.findById(id)
						.map(Store::getAutoOrderEnabled)
						.orElse(false);
	}

	@Override    
	@Transactional
	public void updateAutoOrderEnabled(Integer id, Boolean enable) throws Exception {
		Store store = storeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Store not found: " + id));
        store.setAutoOrderEnabled(enable);		
	}




}
