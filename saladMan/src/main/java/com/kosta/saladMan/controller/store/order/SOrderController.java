package com.kosta.saladMan.controller.store.order;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.saladMan.auth.PrincipalDetails;
import com.kosta.saladMan.dto.purchaseOrder.LowStockItemDto;
import com.kosta.saladMan.dto.purchaseOrder.PurchaseOrderDto;
import com.kosta.saladMan.dto.purchaseOrder.StoreOrderItemDto;
import com.kosta.saladMan.entity.store.Store;
import com.kosta.saladMan.repository.order.StoreIngredientDslRepository;
import com.kosta.saladMan.service.order.OrderService;

@RestController
@RequestMapping("/store")
public class SOrderController {
	
	@Autowired
	private OrderService orderService;
	
	//매장 발주 신청
	@PostMapping("/orderApply")
	public ResponseEntity<List<StoreOrderItemDto>> orderApply(@RequestBody List<StoreOrderItemDto> items,
            @AuthenticationPrincipal PrincipalDetails principal){
        Store storeInfo = principal.getStore(); // JWT에서 store 정보 추출
        System.out.println(storeInfo.getId());
        try {
        	orderService.createOrder(storeInfo,items);
            return new ResponseEntity<>(HttpStatus.OK);
            
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		
	}
	
	//수량미달품목조회
	@GetMapping("/orderApply/lowStock")
	public ResponseEntity<List<LowStockItemDto>> getLowStockItems(@AuthenticationPrincipal PrincipalDetails principal) {
        Integer id = principal.getStore().getId(); // JWT에서 store 정보 추출
        System.out.println(id);
        try {
            List<LowStockItemDto> result = orderService.getLowStockItems(id);
            return new ResponseEntity<>(result,HttpStatus.OK);
            
		} catch (Exception e) {
			e.printStackTrace();

			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

	}
	
	//신청할 재료 리스트
	@GetMapping("/orderApply/items")
	public ResponseEntity<List<StoreOrderItemDto>> getIngredientItems(
			 @AuthenticationPrincipal PrincipalDetails principal,
			    @RequestParam(required = false) String category,
			    @RequestParam(required = false) String keyword) {
        Integer id = principal.getStore().getId(); // JWT에서 store 정보 추출
        System.out.println(id);
        try {
            List<StoreOrderItemDto> result = orderService.getOrderItems(id, category, keyword);
            return new ResponseEntity<>(result,HttpStatus.OK);
            
		} catch (Exception e) {
			e.printStackTrace();

			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

	}
	
	
	@GetMapping("/orderList")
	public ResponseEntity<Page<PurchaseOrderDto>> getOrderList( @AuthenticationPrincipal PrincipalDetails principalDetails,
		    @RequestParam(required = false) String orderType,
		    @RequestParam(required = false) String productName,
		    @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
		    @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
		    @RequestParam(defaultValue = "0") int page,
		    @RequestParam(defaultValue = "10") int size){
	    Integer id = principalDetails.getStore().getId();

        try {
            Page<PurchaseOrderDto> result = orderService.getPagedOrderList(id, orderType, productName, startDate, endDate, page, size);
            return new ResponseEntity<>(result,HttpStatus.OK);
            
		} catch (Exception e) {
			e.printStackTrace();

			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}	}
}
