package com.kosta.saladMan.controller.hq.order;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.saladMan.dto.inventory.IngredientCategoryDto;
import com.kosta.saladMan.dto.inventory.IngredientItemDto;
import com.kosta.saladMan.dto.menu.MenuCategoryDto;
import com.kosta.saladMan.dto.purchaseOrder.PurchaseOrderDto;
import com.kosta.saladMan.dto.purchaseOrder.PurchaseOrderItemDto;
import com.kosta.saladMan.service.order.OrderService;

@RestController
@RequestMapping("/hq")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@GetMapping("/ingredients")
	public ResponseEntity<Map<String, Object>> getAllIngredients(@RequestParam(required = false) String available,
			@RequestParam(required = false) String category, @RequestParam(required = false) String keyword,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "6") int size) throws Exception {
		System.out.println("available: " + available + ", page: " + page + ", size: " + size);
		Boolean availableBoolean = null;

		if ("possible".equals(available)) {
			availableBoolean = true;
		} else if ("impossible".equals(available)) {
			availableBoolean = false;
		}

		Page<IngredientItemDto> ingredientPage = orderService.getIngredientList(availableBoolean, category, keyword,
				page, size);
		System.out.println("조회된 개수: " + ingredientPage.getTotalElements());

		Map<String, Object> result = new HashMap<>();
		result.put("content", ingredientPage.getContent());
		result.put("totalPages", ingredientPage.getTotalPages());
		result.put("number", ingredientPage.getNumber());

		return ResponseEntity.ok(result);
	}

	@PostMapping("/ingredients/{id}/available-toggle")
	public ResponseEntity<Map<String, Object>> toggleIngredientAvailability(@PathVariable Integer id) throws Exception {
		System.out.println(id);
		Boolean updatedAvailable = orderService.toggleIngredientAvailability(id);
		Map<String, Object> result = new HashMap<>();
		result.put("available", updatedAvailable);
		System.out.println(result);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/orderRequestList")
	public ResponseEntity<Map<String, Object>> getOrderRequests(@RequestParam(required = false) String storeName,
			@RequestParam(required = false) String status,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size, Sort.by("orderDateTime").descending());
			Map<String, Object> result = orderService.getOrderListByHq(storeName, status, startDate, endDate, pageable);
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@GetMapping("/orderRequestDetail/{id}")
	public ResponseEntity<List<PurchaseOrderItemDto>> getOrderRequestDetail(@PathVariable Integer id) {
		try {
			System.out.println(id);
			List<PurchaseOrderItemDto> result = orderService.getOrderDetailByHq(id);
			for(PurchaseOrderItemDto dto : result) {
				System.out.println(dto);
			}
			return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}
	
	@PostMapping("/orderRequestDetail")
	public ResponseEntity<Void> updateOrderRequest(@RequestBody List<PurchaseOrderItemDto> items) {
	    try {
	        orderService.updateOrderItems(items);
	        System.out.println("저장완");
	        return ResponseEntity.ok().build();
	    } catch (Exception e) {
	    	e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	    }
	}
	
	@GetMapping("/ingredientCategories")
	public ResponseEntity<List<IngredientCategoryDto>> getIngredientCategories(){
		try {
			List<IngredientCategoryDto> result = orderService.getAllIngredientCategory();
			return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
}
