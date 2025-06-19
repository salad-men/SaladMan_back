package com.kosta.saladMan.controller.hq.order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.saladMan.dto.inventory.IngredientItemDto;
import com.kosta.saladMan.service.order.OrderService;

@RestController
@RequestMapping("/hq")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@GetMapping("/ingredients")
	public ResponseEntity<Map<String, Object>> getAllIngredients(@RequestParam(required = false) String available,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "6") int size) throws Exception {
		System.out.println("available: " + available + ", page: " + page + ", size: " + size);
	    Boolean availableBoolean = null;
	    
	    if ("possible".equals(available)) {
	        availableBoolean = true;
	    } else if ("impossible".equals(available)) {
	        availableBoolean = false;
	    }
	    
		Page<IngredientItemDto> ingredientPage = orderService.getIngredientList(availableBoolean, page, size);
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
}
