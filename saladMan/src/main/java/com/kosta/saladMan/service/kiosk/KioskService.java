package com.kosta.saladMan.service.kiosk;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kosta.saladMan.dto.kiosk.PaymentConfirmDto;
import com.kosta.saladMan.dto.kiosk.PaymentPrepareDto;
import com.kosta.saladMan.dto.kiosk.PaymentPrepareResponseDto;
import com.kosta.saladMan.dto.menu.KioskMenuDto;
import com.kosta.saladMan.dto.menu.MenuCategoryDto;

public interface KioskService {
	//메뉴 페이징처리
	Page<KioskMenuDto> getStoreMenuByKiosk(Integer storeId, Integer categoryId, String categoryName, Pageable pageable)	throws Exception;
	//카테고리 불러오기
	List<MenuCategoryDto> getAllCategory() throws Exception;
	
    PaymentPrepareResponseDto preparePayment(PaymentPrepareDto paymentPrepareDto) throws Exception;
    void confirmPayment(PaymentConfirmDto paymentConfirmDto) throws Exception;
}
