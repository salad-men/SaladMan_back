package com.kosta.saladMan.service.kiosk;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.client.RestTemplate;

import com.kosta.saladMan.dto.kiosk.PaymentConfirmDto;
import com.kosta.saladMan.dto.kiosk.PaymentPrepareDto;
import com.kosta.saladMan.dto.kiosk.PaymentPrepareResponseDto;
import com.kosta.saladMan.dto.menu.KioskMenuDto;
import com.kosta.saladMan.dto.menu.MenuCategoryDto;
import com.kosta.saladMan.entity.inventory.InventoryRecord;
import com.kosta.saladMan.entity.inventory.StoreIngredient;
import com.kosta.saladMan.entity.menu.MenuCategory;
import com.kosta.saladMan.entity.menu.MenuIngredient;
import com.kosta.saladMan.entity.payment.Payment;
import com.kosta.saladMan.entity.saleOrder.SaleOrder;
import com.kosta.saladMan.entity.saleOrder.SaleOrderItem;
import com.kosta.saladMan.entity.store.Store;
import com.kosta.saladMan.repository.StoreRepository;
import com.kosta.saladMan.repository.inventory.InventoryRecordRepository;
import com.kosta.saladMan.repository.inventory.StoreIngredientRepository;
import com.kosta.saladMan.repository.kiosk.PaymentRepository;
import com.kosta.saladMan.repository.menu.MenuCategoryRepository;
import com.kosta.saladMan.repository.menu.SMenuDslRepository;
import com.kosta.saladMan.repository.saleOrder.SaleOrderItemRepository;
import com.kosta.saladMan.repository.saleOrder.SaleOrderRepository;
import com.kosta.saladMan.repository.user.MenuIngredientRepository;
import com.kosta.saladMan.util.OutOfStockException;

@Service
public class KioskServiceImpl implements KioskService {
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private SMenuDslRepository sMenuDslRepository;
	@Autowired
	private MenuCategoryRepository menuCategoryRepository;

	@Autowired
	private SaleOrderRepository saleOrderRepository;
	@Autowired
	private SaleOrderItemRepository saleOrderItemRepository;
	@Autowired
	private PaymentRepository paymentRepository;
	@Autowired
	private StoreRepository storeRepository;

	@Autowired
	private StoreIngredientRepository storeIngredientRepository;

	@Autowired
	private MenuIngredientRepository menuIngredientRepository;

	@Autowired
	private InventoryRecordRepository inventoryRecordRepository;
	
	@Autowired
	private OrderCancellationService orderCancellationService;

	@Value("${toss.test-secret-key}")
	private String tossSecretKey;

	@Value("${toss.success-url}")
	private String successUrl;

	@Value("${toss.fail-url}")
	private String failUrl;

	@Autowired
	private RestTemplate restTemplate;

	@Override
	public Page<KioskMenuDto> getStoreMenuByKiosk(Integer storeId, Integer categoryId, String categoryName,
			Pageable pageable) throws Exception {
		return sMenuDslRepository.findMenuWithStoreStatusByKiosk(storeId, categoryId, categoryName, pageable);
	}

	@Override
	public List<MenuCategoryDto> getAllCategory() {
		List<MenuCategoryDto> categoryDto = menuCategoryRepository.findAll().stream().map(MenuCategory::toDto)
				.collect(Collectors.toList());

		return categoryDto;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public PaymentPrepareResponseDto preparePayment(PaymentPrepareDto dto) {

		System.out.println("saleOrder생성-----------");
		// SaleOrder 생성
		Store store = storeRepository.findById(dto.getStoreId())
				.orElseThrow(() -> new RuntimeException("Store not found"));

		System.out.println("store정보:" + dto.getStoreId());

		int totalPrice = dto.getItems().stream().mapToInt(i -> i.getPrice() * i.getQuantity()).sum();

		SaleOrder saleOrder = SaleOrder.builder().store(store).status("주문완료").totalPrice(totalPrice).build();
		saleOrderRepository.save(saleOrder);

		SaleOrder found = saleOrderRepository.findById(saleOrder.getId())
				.orElseThrow(() -> new RuntimeException("왜 없어?"));

		System.out.println("저장된 saleOrder id = " + saleOrder.getId());

		System.out.println("saleOrder저장-----------");

		// SaleOrderItem 저장
		for (PaymentPrepareDto.ItemDto i : dto.getItems()) {
			SaleOrderItem item = SaleOrderItem.builder().saleOrder(saleOrder).menuId(i.getMenuId())
					.quantity(i.getQuantity()).price(i.getPrice()).build();
			saleOrderItemRepository.save(item);
			System.out.println("저장된 SaleOrderItem id = " + item.getId());

		}

		System.out.println("saleOrderItem 저장 완-----------");

		// Payment(빈 값) 저장
		Payment payment = Payment.builder().saleOrder(saleOrder).status("READY").amount(totalPrice).build();
		paymentRepository.save(payment);

		System.out.println("롤백 여부: " + TransactionAspectSupport.currentTransactionStatus().isRollbackOnly());
		System.out.println("=== FLUSH ===");
		// 반환
		return new PaymentPrepareResponseDto("ORDER-" + saleOrder.getId(), totalPrice);
	}

	@Transactional
	public void confirmPayment(PaymentConfirmDto dto) {

		System.out.println("confirmPayment 진입-------------------------");

		// --- [1] Toss 결제 승인
		Map<String, Object> payload = Map.of("paymentKey", dto.getPaymentKey(), "orderId", dto.getOrderId(), "amount",
				dto.getAmount());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String auth = Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));
		headers.set("Authorization", "Basic " + auth);

		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

		ResponseEntity<Map> res = restTemplate.postForEntity("https://api.tosspayments.com/v1/payments/confirm", entity,
				Map.class);

		Map body = res.getBody();
		String method = (String) body.get("method");
		String approvedAtStr = (String) body.get("approvedAt");

		OffsetDateTime approvedAt = OffsetDateTime.parse(approvedAtStr);
		System.out.println("SaleOrder조회<<<<<<<<<<<<<<<<<<<<<<<<<");
		// --- [2] SaleOrder 조회
		Integer orderPk = Integer.parseInt(dto.getOrderId().replace("ORDER-", ""));
		SaleOrder saleOrder = saleOrderRepository.findById(orderPk)
				.orElseThrow(() -> new RuntimeException("주문이 없습니다."));

		// --- [3] Payment 조회
		Payment payment = paymentRepository.findBySaleOrder(saleOrder)
				.orElseThrow(() -> new RuntimeException("결제 정보가 없습니다."));

		// --- [4] Payment 상태 갱신
		payment.setPaymentKey(dto.getPaymentKey());
		payment.setMethod(method);
		payment.setStatus("PAID");
		payment.setApprovedAt(approvedAt.toLocalDateTime());

		saleOrder.setStatus("PAID");

		// --- [5] 재고 차감
		List<SaleOrderItem> saleItems = saleOrderItemRepository.findBySaleOrder(saleOrder);
		try {
			for (SaleOrderItem item : saleItems) {
				// 해당 메뉴의 재료 리스트 가져오기
				List<MenuIngredient> ingredients = menuIngredientRepository.findByMenuId(item.getMenuId());
				System.out.println(ingredients);
				for (MenuIngredient mi : ingredients) {
					List<StoreIngredient> siList = storeIngredientRepository
							.findByStoreAndIngredientId(saleOrder.getStore(), mi.getIngredient().getId());
					System.out.println(mi + "," + siList);
					if (siList.isEmpty()) {
						throw new OutOfStockException("재고 정보가 없습니다.");
					}

					StoreIngredient si = siList.get(0);

					int deduction = mi.getQuantity() * item.getQuantity();
					if (si.getQuantity() < deduction) {
						throw new OutOfStockException("재고 부족: " + mi.getIngredient().getName());
					}

					si.setQuantity(si.getQuantity() - deduction);

					System.out.println("출고 저장<<<<<<<<<<<<<<<<<<<<<<<<");
					// ★ 출고 기록 저장
					InventoryRecord record = new InventoryRecord();
					record.setChangeType("출고");
					record.setDate(LocalDateTime.now());
					record.setMemo("매장 판매");
					record.setQuantity(deduction);
					record.setIngredient(mi.getIngredient());
					record.setStore(saleOrder.getStore());

					inventoryRecordRepository.save(record);
				}
			}
		} catch (OutOfStockException ex) {
			ex.printStackTrace();
			orderCancellationService.markOrderCanceled(saleOrder.getId());
		    throw ex; // 이거 다시 살리기

		}

	}
	
}
