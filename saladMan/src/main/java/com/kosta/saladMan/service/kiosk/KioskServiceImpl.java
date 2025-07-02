package com.kosta.saladMan.service.kiosk;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.kosta.saladMan.dto.menu.KioskMenuDto;
import com.kosta.saladMan.dto.menu.MenuCategoryDto;
import com.kosta.saladMan.entity.menu.MenuCategory;
import com.kosta.saladMan.repository.menu.MenuCategoryRepository;
import com.kosta.saladMan.repository.menu.SMenuDslRepository;

@Service
public class KioskServiceImpl implements KioskService {

	@Autowired
	private SMenuDslRepository sMenuDslRepository;
	@Autowired
	private MenuCategoryRepository menuCategoryRepository;

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

}
