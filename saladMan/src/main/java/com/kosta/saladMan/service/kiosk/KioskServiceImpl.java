package com.kosta.saladMan.service.kiosk;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.kosta.saladMan.dto.kiosk.PaymentConfirmDto;
import com.kosta.saladMan.dto.kiosk.PaymentPrepareDto;
import com.kosta.saladMan.dto.kiosk.PaymentPrepareResponseDto;
import com.kosta.saladMan.dto.menu.KioskMenuDto;
import com.kosta.saladMan.dto.menu.MenuCategoryDto;
import com.kosta.saladMan.entity.menu.MenuCategory;
import com.kosta.saladMan.entity.payment.Payment;
import com.kosta.saladMan.entity.saleOrder.SaleOrder;
import com.kosta.saladMan.entity.saleOrder.SaleOrderItem;
import com.kosta.saladMan.entity.store.Store;
import com.kosta.saladMan.repository.StoreRepository;
import com.kosta.saladMan.repository.kiosk.PaymentRepository;
import com.kosta.saladMan.repository.menu.MenuCategoryRepository;
import com.kosta.saladMan.repository.menu.SMenuDslRepository;
import com.kosta.saladMan.repository.saleOrder.SaleOrderItemRepository;
import com.kosta.saladMan.repository.saleOrder.SaleOrderRepository;

@Service
public class KioskServiceImpl implements KioskService {

	@Autowired
	private SMenuDslRepository sMenuDslRepository;
	@Autowired
	private MenuCategoryRepository menuCategoryRepository;

	@Autowired
    private  SaleOrderRepository saleOrderRepository;
	@Autowired
    private  SaleOrderItemRepository saleOrderItemRepository;
    @Autowired
    private  PaymentRepository paymentRepository;
    @Autowired
    private  StoreRepository storeRepository;
    
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
		List<MenuCategoryDto> categoryDto = menuCategoryRepository.findAll().stream()
			    .map(MenuCategory::toDto)
			    .collect(Collectors.toList());
		
		return categoryDto;
	}

    @Transactional
    public PaymentPrepareResponseDto preparePayment(PaymentPrepareDto dto) {

    	  // SaleOrder 생성
        Store store = storeRepository.findById(dto.getStoreId())
            .orElseThrow(() -> new RuntimeException("Store not found"));

        int totalPrice = dto.getItems().stream()
            .mapToInt(i -> i.getPrice() * i.getQuantity())
            .sum();

        SaleOrder saleOrder = SaleOrder.builder()
            .store(store)
            .status("READY")
            .totalPrice(totalPrice)
            .build();
        saleOrderRepository.save(saleOrder);

        // SaleOrderItem 저장
        for (PaymentPrepareDto.ItemDto i : dto.getItems()) {
            SaleOrderItem item = SaleOrderItem.builder()
                .saleOrder(saleOrder)
                .menuId(i.getMenuId())
                .quantity(i.getQuantity())
                .price(i.getPrice())
                .build();
            saleOrderItemRepository.save(item);
        }

        // Payment(빈 값) 저장
        Payment payment = Payment.builder()
            .saleOrder(saleOrder)
            .status("READY")
            .amount(totalPrice)
            .build();
        paymentRepository.save(payment);

        // 반환
        return new PaymentPrepareResponseDto(
            "ORDER-" + saleOrder.getId(),
            totalPrice
        );
    }

    @Transactional
    public void confirmPayment(PaymentConfirmDto dto) {
    	Map<String, Object> payload = Map.of(
    	        "paymentKey", dto.getPaymentKey(),
    	        "orderId", dto.getOrderId(),
    	        "amount", dto.getAmount()
    	    );

    	    HttpHeaders headers = new HttpHeaders();
    	    headers.setContentType(MediaType.APPLICATION_JSON);
    	    String auth = Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));
    	    headers.set("Authorization", "Basic " + auth);

    	    ResponseEntity<Map> res = restTemplate.postForEntity(
    	        "https://api.tosspayments.com/v1/payments/confirm",
    	        new HttpEntity<>(payload, headers),
    	        Map.class
    	    );

    	    Map body = res.getBody();
    	    String method = (String) body.get("method");
    	    String approvedAt = (String) body.get("approvedAt");

    	    Integer orderPk = Integer.parseInt(dto.getOrderId().replace("ORDER-", ""));
    	    SaleOrder saleOrder = saleOrderRepository.findById(orderPk)
    	        .orElseThrow(() -> new RuntimeException("주문 없음"));

    	    Payment payment = paymentRepository.findBySaleOrder(saleOrder)
    	        .orElseThrow(() -> new RuntimeException("결제 정보 없음"));

    	    payment.setPaymentKey(dto.getPaymentKey());
    	    payment.setMethod(method);
    	    payment.setStatus("PAID");
    	    payment.setApprovedAt(LocalDateTime.parse(approvedAt));

    	    saleOrder.setStatus("PAID");
    }

}
