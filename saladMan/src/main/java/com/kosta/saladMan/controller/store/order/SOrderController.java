package com.kosta.saladMan.controller.store.order;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.saladMan.auth.PrincipalDetails;
import com.kosta.saladMan.dto.purchaseOrder.FixedOrderItemDto;
import com.kosta.saladMan.dto.purchaseOrder.LowStockItemDto;
import com.kosta.saladMan.dto.purchaseOrder.PurchaseOrderDto;
import com.kosta.saladMan.dto.purchaseOrder.PurchaseOrderItemDto;
import com.kosta.saladMan.dto.purchaseOrder.StoreOrderItemDto;
import com.kosta.saladMan.entity.store.Store;
import com.kosta.saladMan.repository.order.StoreIngredientDslRepository;
import com.kosta.saladMan.service.order.OrderService;

@RestController
@RequestMapping("/store")
public class SOrderController {

	@Autowired
	private OrderService orderService;

	// 매장 발주 신청
	@PostMapping("/orderApply")
	public ResponseEntity<Map<String, Integer>> orderApply(@RequestBody List<StoreOrderItemDto> items,
			@AuthenticationPrincipal PrincipalDetails principal) {
		Store storeInfo = principal.getStore(); // JWT에서 store 정보 추출
		System.out.println(storeInfo.getId());
		try {
			Integer orderId= orderService.createOrder(storeInfo, items,"수기발주");
			return new ResponseEntity<>(Map.of("orderId",orderId),HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

	}

	// 수량미달품목조회
	@GetMapping("/orderApply/lowStock")
	public ResponseEntity<List<LowStockItemDto>> getLowStockItems(@AuthenticationPrincipal PrincipalDetails principal) {
		Integer id = principal.getStore().getId(); // JWT에서 store 정보 추출
		System.out.println(id);
		try {
			List<LowStockItemDto> result = orderService.getLowStockItems(id);
			return new ResponseEntity<>(result, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();

			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

	}

	// 신청할 재료 리스트
	@GetMapping("/orderApply/items")
	public ResponseEntity<List<StoreOrderItemDto>> getIngredientItems(
			@AuthenticationPrincipal PrincipalDetails principal, @RequestParam(required = false) String category,
			@RequestParam(required = false) String keyword) {
		Integer id = principal.getStore().getId(); // JWT에서 store 정보 추출
		System.out.println(id);
		try {
			List<StoreOrderItemDto> result = orderService.getOrderItems(id, category, keyword);
			return new ResponseEntity<>(result, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();

			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

	}
	
	//발주 신청 목록
	@GetMapping("/orderList")
	public ResponseEntity<Page<PurchaseOrderDto>> getOrderList(
			@AuthenticationPrincipal PrincipalDetails principalDetails,
			@RequestParam(required = false) String orderType, @RequestParam(required = false) String productName,@RequestParam(required = false) String status,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		Integer id = principalDetails.getStore().getId();

		try {
			Page<PurchaseOrderDto> result = orderService.getPagedOrderList(id, orderType, productName, status, startDate,
					endDate, page, size);
			return new ResponseEntity<>(result, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();

			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	//발주 검수 페이지
	@GetMapping("/stockInspection/{id}")
	public ResponseEntity<List<PurchaseOrderItemDto>> getInspection(@PathVariable Integer id){
		try {
			System.out.println(id);
			List<PurchaseOrderItemDto> result = orderService.getInspectionInfo(id);
			for(PurchaseOrderItemDto dto : result) {
				System.out.println(dto);
			}
			return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}
	
	//발주 상태 저장
	@PostMapping("/stockInspection")
	public ResponseEntity<Void> saveInspection(@RequestBody List<PurchaseOrderItemDto> items) {
	    try {
			orderService.saveInspectionResults(items);
		    return ResponseEntity.ok().build();
	    } catch (Exception e) {
	    	e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	    }

	}
	
	//발주 상세
	@GetMapping("/orderDetail/{id}")
	public ResponseEntity<List<PurchaseOrderItemDto>> getOrderList(@PathVariable Integer id){
		try {
			System.out.println(id);
			List<PurchaseOrderItemDto> result = orderService.getInspectionInfo(id);
			for(PurchaseOrderItemDto dto : result) {
				System.out.println(dto);
			}
			return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}
	
	//매장별 발주 설정 조회
	@GetMapping("/orderSettings")
	public ResponseEntity<List<FixedOrderItemDto>> getSettings( @AuthenticationPrincipal PrincipalDetails principalDetails){
		Integer id =  principalDetails.getStore().getId();
		try {
			System.out.println(id);
			List<FixedOrderItemDto> result = orderService.getSettings(id);
			for(FixedOrderItemDto dto : result) {
				System.out.println(dto);
			}
			return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}
	//매장별 발주 설정 저장

	@PostMapping("/orderSettings")
	public ResponseEntity<Void> saveSettings(@AuthenticationPrincipal PrincipalDetails principalDetails,
			@RequestBody List<FixedOrderItemDto> settings) {
		Integer id =  principalDetails.getStore().getId();

		try {
			orderService.saveSettings(id,settings);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}
	
	//자동발주 사용 여부
	@GetMapping("/autoOrderEnabled")
	public ResponseEntity<Map<String, Boolean>> getAutoOrderEnabled(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		Integer id =  principalDetails.getStore().getId();
		try {
			boolean enabled = orderService.getAutoOrderEnabled(id);
	        return ResponseEntity.ok(Collections.singletonMap("enabled", enabled));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}

    }
	
    @PostMapping("/autoOrderEnabled")
    public ResponseEntity<Void> setAutoOrderEnabled(
    		@AuthenticationPrincipal PrincipalDetails principalDetails,
        @RequestBody Map<String, Boolean> body
    ) {
		Integer id =  principalDetails.getStore().getId();
		try {
			orderService.updateAutoOrderEnabled(id, body.get("enabled"));
	        return ResponseEntity.ok().build();
	        
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
        
    }
}
