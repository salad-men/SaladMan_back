package com.kosta.saladMan.controller.store.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.saladMan.auth.PrincipalDetails;
import com.kosta.saladMan.dto.purchaseOrder.LowStockItemDto;
import com.kosta.saladMan.dto.purchaseOrder.StoreOrderItemDto;
import com.kosta.saladMan.repository.order.StoreIngredientDslRepository;
import com.kosta.saladMan.service.order.OrderService;

@RestController
@RequestMapping("/store")
public class SOrderController {
	
	@Autowired
	private OrderService orderService;
	
	@GetMapping("/orderApply/lowStock")
	public ResponseEntity<List<LowStockItemDto>> getLowStockItems(@AuthenticationPrincipal PrincipalDetails principal) {
        Integer id = principal.getStore().getId(); // JWT에서 store 정보 추출
        System.out.println(id);
        try {
            List<LowStockItemDto> result = orderService.getLowStockItems(id);
            return new ResponseEntity<>(result,HttpStatus.OK);
            
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

	}
	
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
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

	}
}
