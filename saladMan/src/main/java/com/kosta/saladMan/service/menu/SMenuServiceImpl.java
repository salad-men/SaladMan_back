package com.kosta.saladMan.service.menu;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.kosta.saladMan.dto.menu.RecipeDto;
import com.kosta.saladMan.dto.menu.StoreMenuStatusDto;
import com.kosta.saladMan.dto.menu.TotalMenuDto;
import com.kosta.saladMan.entity.menu.StoreMenu;
import com.kosta.saladMan.entity.menu.TotalMenu;
import com.kosta.saladMan.entity.store.Store;
import com.kosta.saladMan.repository.MenuRepository;
import com.kosta.saladMan.repository.menu.SMenuDslRepository;
import com.kosta.saladMan.repository.menu.SMenuRepository;
import com.kosta.saladMan.util.PageInfo;
@Service
public class SMenuServiceImpl implements SMenuService {
	
	@Autowired
	private MenuRepository menuRepository;
	@Autowired
	private SMenuRepository sMenuRepository;
	@Autowired
	private SMenuDslRepository sMenuDslRepository;

	@Override
	public List<TotalMenuDto> getTotalMenu(PageInfo pageInfo, String sort) throws Exception {
		Sort sorting = Sort.by(Sort.Direction.ASC, "createdAt"); // 기본값
		
		if ("release_desc".equals(sort)) {
	        sorting = Sort.by(Sort.Direction.DESC, "createdAt");
	    } else if ("release_asc".equals(sort)) {
	        sorting = Sort.by(Sort.Direction.ASC, "createdAt");
	    } else if ("name_desc".equals(sort)) {
	        sorting = Sort.by(Sort.Direction.DESC, "name");
	    } else if ("name_asc".equals(sort)) {
	        sorting = Sort.by(Sort.Direction.ASC, "name");
	    } else if ("price_desc".equals(sort)) {
	        sorting = Sort.by(Sort.Direction.DESC, "salePrice");
	    } else if ("price_asc".equals(sort)) {
	        sorting = Sort.by(Sort.Direction.ASC, "salePrice");
	    }
		
		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 10, sorting);
		Page<TotalMenu> pages = menuRepository.findAll(pageRequest);
		
		pageInfo.setAllPage(pages.getTotalPages());
		int startPage = (pageInfo.getAllPage() - 1) / 10 * 10 + 1;
		int endPage = Math.min(startPage + 9, pageInfo.getAllPage());
		pageInfo.setStartPage(startPage);
		pageInfo.setEndPage(endPage);

	    return pages.getContent()
	            .stream()
	            .map(TotalMenuDto::fromEntity)
	            .collect(Collectors.toList());
	}

	@Override
	public List<StoreMenuStatusDto> getStoreStuatus(Integer storeId) throws Exception {
		return sMenuDslRepository.findMenuWithStoreStatus(storeId);
	}

//	@Override
//	public boolean toggleMenuStatus(Integer storeId, Integer menuId) throws Exception {
//	    Optional<StoreMenu> optional = menuRepository.findByStoreIdAndMenuId(storeId, menuId);
//
//	    if (optional.isPresent()) {
//	        StoreMenu storeMenu = optional.get();
//	        storeMenu.setStatus(!storeMenu.getStatus());
//	        menuRepository.save(storeMenu);
//	    } else {
//	        Store store = storeRepository.findById(storeId)
//	                .orElseThrow(() -> new Exception("매장 정보가 없습니다."));
//
//	        TotalMenu menu = menuRepository.findById(menuId)
//	                .orElseThrow(() -> new Exception("메뉴 정보가 없습니다."));
//
//	        StoreMenu newStoreMenu = StoreMenu.builder()
//	                .store(store)
//	                .menu(menu)
//	                .status(true)
//	                .build();
//
//	        menuRepository.save(newStoreMenu);
//	    }
//	    return true;
//	}
	
	@Override
	public List<RecipeDto> getAllMenuRecipes() throws Exception {
		return sMenuDslRepository.findAllMenusWithIngredients();
	}

}
