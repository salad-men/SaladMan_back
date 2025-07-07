package com.kosta.saladMan.service.kiosk;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.saladMan.dto.kiosk.PaymentConfirmDto;
import com.kosta.saladMan.dto.kiosk.PaymentPrepareDto;
import com.kosta.saladMan.dto.kiosk.PaymentPrepareResponseDto;
import com.kosta.saladMan.dto.menu.KioskMenuDto;
import com.kosta.saladMan.dto.menu.MenuCategoryDto;
import com.kosta.saladMan.repository.kiosk.PaymentRepository;
import com.kosta.saladMan.repository.saleOrder.SaleOrderRepository;

public interface KioskService {
    
	//메뉴 페이징처리
	Page<KioskMenuDto> getStoreMenuByKiosk(Integer storeId, Integer categoryId, String categoryName, Pageable pageable)	throws Exception;
	//카테고리 불러오기
	List<MenuCategoryDto> getAllMenuCategory() throws Exception;
	
    PaymentPrepareResponseDto preparePayment(PaymentPrepareDto paymentPrepareDto) throws Exception;
    void confirmPayment(PaymentConfirmDto paymentConfirmDto) throws Exception;

}
