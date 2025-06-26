package com.kosta.saladMan.controller.store.storeInquiry;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.saladMan.auth.PrincipalDetails;
import com.kosta.saladMan.dto.storeInquiry.FindStoreDto;
import com.kosta.saladMan.service.storeInquiry.StoreInquiryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
public class StoreInquiryController {
	
	private final StoreInquiryService storeInquiryService;
	
	@GetMapping("/findOtherStore")
	public ResponseEntity<List<FindStoreDto>> findOtherStores(@AuthenticationPrincipal PrincipalDetails principal){
		 Integer myStoreId = principal.getStore().getId();
	     List<FindStoreDto> stores = storeInquiryService.findOtherStores(myStoreId);
	     return ResponseEntity.ok(stores);
	}
}
