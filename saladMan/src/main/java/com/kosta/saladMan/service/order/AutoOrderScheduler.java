package com.kosta.saladMan.service.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.kosta.saladMan.dto.alarm.AlarmDto;
import com.kosta.saladMan.dto.purchaseOrder.StoreOrderItemDto;
import com.kosta.saladMan.entity.alarm.AlarmMsg;
import com.kosta.saladMan.entity.inventory.HqIngredient;
import com.kosta.saladMan.entity.purchaseOrder.FixedOrderItem;
import com.kosta.saladMan.entity.purchaseOrder.FixedOrderTemplate;
import com.kosta.saladMan.entity.purchaseOrder.PurchaseOrder;
import com.kosta.saladMan.entity.purchaseOrder.PurchaseOrderItem;
import com.kosta.saladMan.entity.store.Store;
import com.kosta.saladMan.repository.StoreRepository;
import com.kosta.saladMan.repository.alarm.AlarmMsgRepository;
import com.kosta.saladMan.repository.inventory.HqIngredientRepository;
import com.kosta.saladMan.repository.order.FixedOrderItemRepository;
import com.kosta.saladMan.repository.order.FixedOrderTemplateRepository;
import com.kosta.saladMan.repository.order.PurchaseOrderRepository;
import com.kosta.saladMan.service.alarm.FcmMessageService;

@Service
public class AutoOrderScheduler {
	@Autowired
	private StoreRepository storeRepository;
	@Autowired
	private FixedOrderTemplateRepository fixedOrderTemplateRepository;
	@Autowired
	private FixedOrderItemRepository fixedOrderItemRepository;
	@Autowired
	private PurchaseOrderRepository purchaseOrderRepository;
	@Autowired
	private HqIngredientRepository hqIngredientRepository;

	@Autowired
	private OrderService orderService;
	
	//fcm알람
	@Autowired
	private AlarmMsgRepository alarmMsgRepository;
	@Autowired
	private FcmMessageService fcmMessageService;

	// 매일 오후 5시
	@Scheduled(cron = "0 0 17 * * *")
	@Transactional
	public void createAutoOrders() {

		// 1. 자동발주 사용 매장 조회
		List<Store> stores = storeRepository.findByAutoOrderEnabledTrue();

		for (Store store : stores) {
			System.out.println("자동발주 매장: " + store.getName());

			// 2. 매장별 템플릿 조회
			Optional<FixedOrderTemplate> templateOpt = fixedOrderTemplateRepository.findByStoreId(store.getId());
			if (templateOpt.isEmpty()) {
				System.out.println("템플릿 없음: " + store.getName());
				continue;
			}
			FixedOrderTemplate template = templateOpt.get();

			// 3. 품목 조회
			List<FixedOrderItem> itemList = fixedOrderItemRepository
					.findByFixedOrderTemplateIdAndAutoOrderEnabledTrue(template.getId());
			if (itemList.isEmpty()) {
				System.out.println("자동발주 품목 없음: " + store.getName());
				continue;
			}

			List<StoreOrderItemDto> orderItemList = itemList.stream().map(fixedItem -> {
			    try {
			        Integer ingredientId = fixedItem.getIngredient().getId();
			        Integer quantity = fixedItem.getAutoOrderQty();

			        HqIngredient hq = hqIngredientRepository
			            .findTopByIngredientIdOrderByReceivedDateDescIdDesc(ingredientId)
			            .orElseThrow(() -> new RuntimeException("HQ 재료 정보 없음: " + ingredientId));

			        Integer unitCost = hq.getUnitCost();
			        Integer minimumUnit = hq.getMinimumOrderUnit();
			        Integer totalPrice = quantity / minimumUnit * unitCost;

			        return StoreOrderItemDto.builder()
			            .ingredientId(ingredientId)
			            .quantity(quantity)
			            .unitCost(unitCost)
			            .totalPrice(totalPrice)
			            .build();

			    } catch (Exception e) {
			        System.err.println("[" + store.getName() + "] 품목 처리 실패 - 재료 ID: "
			            + fixedItem.getIngredient().getId() + ", 사유: " + e.getMessage());
			        
			        AlarmMsg alarmMsg = alarmMsgRepository.findById(3)
			                .orElseThrow(() -> new RuntimeException("알림 메시지 없음"));
			        
			        AlarmDto alarmDto = new AlarmDto();
			        alarmDto.setStoreId(store.getId());
			        alarmDto.setTitle(alarmMsg.getTitle());
			        alarmDto.setContent(alarmMsg.getContent());
			        fcmMessageService.sendAlarm(alarmDto);
			        System.out.println("AutoOrderFAIL-Alarm :"+alarmDto);
			        
			        return null;
			    }
			}).filter(Objects::nonNull)
			  .collect(Collectors.toList());
			
			if (orderItemList.isEmpty()) {
			    System.err.println("[" + store.getName() + "] 유효한 발주 품목이 없어 자동발주 생략");
			    continue;
			}

			try {
				orderService.createOrder(store, orderItemList, "자동발주");
				System.out.println(store.getName() + " 자동발주 생성 완료");
				
				AlarmMsg alarmMsg = alarmMsgRepository.findById(2)
		                .orElseThrow(() -> new RuntimeException("알림 메시지 없음"));
		        
		        AlarmDto alarmDto = new AlarmDto();
		        alarmDto.setStoreId(store.getId());
		        alarmDto.setTitle(alarmMsg.getTitle());
		        alarmDto.setContent(alarmMsg.getContent());
		        fcmMessageService.sendAlarm(alarmDto);
		        System.out.println("AutoOrder-Alarm :"+alarmDto);
		        
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println(store.getName() + " 자동발주 실패: " + e.getMessage());
			}

			// 여기선 repository.save()는 필요없이 order.getItems().add()로 해도 됨
			// cascade = CascadeType.ALL 걸려있다면 order 저장 시 함께 저장

		}
	}

}
