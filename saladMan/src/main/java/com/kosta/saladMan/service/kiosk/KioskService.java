package com.kosta.saladMan.service.kiosk;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kosta.saladMan.dto.menu.KioskMenuDto;
import com.kosta.saladMan.dto.menu.MenuCategoryDto;

public interface KioskService {
	Page<KioskMenuDto> getStoreMenuByKiosk(Integer storeId, Integer categoryId, String categoryName, Pageable pageable)	throws Exception;
	List<MenuCategoryDto> getAllCategory();
}
