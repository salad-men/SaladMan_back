package com.kosta.saladMan.service.order;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.kosta.saladMan.dto.inventory.IngredientItemDto;
import com.kosta.saladMan.dto.purchaseOrder.LowStockItemDto;
import com.kosta.saladMan.dto.purchaseOrder.PurchaseOrderDto;
import com.kosta.saladMan.dto.purchaseOrder.PurchaseOrderItemDto;
import com.kosta.saladMan.dto.purchaseOrder.StoreOrderItemDto;
import com.kosta.saladMan.entity.inventory.Ingredient;
import com.kosta.saladMan.entity.purchaseOrder.PurchaseOrder;
import com.kosta.saladMan.entity.purchaseOrder.PurchaseOrderItem;
import com.kosta.saladMan.entity.store.Store;
import com.kosta.saladMan.repository.inventory.IngredientRepository;
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
	private StoreIngredientDslRepository storeIngredientDslRepository;

	@Autowired
	private PurchaseOrderRepository purchaseOrderRepository;

	@Autowired
	private PurchaseOrderItemRepository purchaseOrderItemRepository;

	@Autowired
	private PuchaseOrderDslRepository purchaseOrderDslRepository;

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
		return purchaseOrderDslRepository.orderApplyDetail(purchaseOrderId);
	}
	
	//발주 신청 수락
	@Transactional
	@Override
	public void updateOrderItems(List<PurchaseOrderItemDto> items) throws Exception {
	    if (items == null || items.isEmpty()) return;
	    
	    Integer orderId = items.get(0).getPurchaseOrderId();
	    List<PurchaseOrderItem> orderItems = purchaseOrderItemRepository.findByPurchaseOrderId(orderId);
	    
	    for(PurchaseOrderItem item : orderItems ) {
	    	PurchaseOrderItemDto dto = items.stream()
	    			.filter(d -> d.getId().equals(item.getId()))
	    			.findFirst().orElse(null);
	    	if(dto != null) {
	    		item.setApprovalStatus(dto.getApprovalStatus());
	    		item.setRejectionReason(dto.getRejectionReason());
	    	}
	    }
	 // orderStatus 판단
	    Long approved = orderItems.stream().filter(i -> "승인".equals(i.getApprovalStatus())).count();
	    Long rejected = orderItems.stream().filter(i -> "반려".equals(i.getApprovalStatus())).count();

	    PurchaseOrder order = orderItems.get(0).getPurchaseOrder();
	    if (approved > 0 && rejected > 0) {
	        order.setOrderStatus("부분승인");
	    } else if (approved > 0) {
	        order.setOrderStatus("승인");
	    } else if (approved<=0){
	        order.setOrderStatus("반려");
	    }

	    // status = 입고완료 처리 (반려가 아닌 것이 하나라도 있다면)
	    if (approved > 0) {
	        order.setStatus("입고완료");
	    }
	    else if (approved<=0) {
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
	public void createOrder(Store storeInfo, List<StoreOrderItemDto> items) throws Exception {
		int total = items.stream().mapToInt(item -> {
			Integer qty = item.getQuantity();
			Integer cost = item.getUnitCost();
			return (qty == null || cost == null) ? 0 : item.getItemPrice();
		}).sum();
		PurchaseOrder order = PurchaseOrder.builder().store(storeInfo).orderDateTime(LocalDateTime.now()).status("대기중")
				.requestedBy(storeInfo.getName()).totalPrice(total).purType("수기발주").build();
		purchaseOrderRepository.save(order);

		for (StoreOrderItemDto sOrderDto : items) {
			Ingredient ingredient = ingredientRepository.findById(sOrderDto.getIngredientId())
					.orElseThrow(() -> new Exception("존재하지 않는 재료"));

			PurchaseOrderItem orderItem = new PurchaseOrderItem();
			orderItem.setPurchaseOrder(order);
			orderItem.setIngredient(ingredient);
			orderItem.setOrderedQuantity(sOrderDto.getQuantity());
			orderItem.setReceivedQuantity(0);
			orderItem.setTotalPrice(sOrderDto.getQuantity() * sOrderDto.getUnitCost());
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

	@Override
	public void saveInspectionResults(List<PurchaseOrderItemDto> items) throws Exception {
		
	}




}
