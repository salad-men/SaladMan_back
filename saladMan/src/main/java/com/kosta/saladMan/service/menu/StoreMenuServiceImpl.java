package com.kosta.saladMan.service.menu;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.saladMan.controller.common.S3Uploader;
import com.kosta.saladMan.dto.alarm.AlarmDto;
import com.kosta.saladMan.dto.alarm.SendAlarmDto;
import com.kosta.saladMan.dto.inventory.IngredientDto;
import com.kosta.saladMan.dto.menu.IngredientInfoDto;
import com.kosta.saladMan.dto.menu.MenuCategoryDto;
import com.kosta.saladMan.dto.menu.MenuIngredientDto;
import com.kosta.saladMan.dto.menu.MenuRegisterDto;
import com.kosta.saladMan.dto.menu.RecipeDto;
import com.kosta.saladMan.dto.menu.StoreMenuStatusDto;
import com.kosta.saladMan.dto.menu.TotalMenuDto;
import com.kosta.saladMan.entity.alarm.AlarmMsg;
import com.kosta.saladMan.entity.inventory.Ingredient;
import com.kosta.saladMan.entity.menu.MenuCategory;
import com.kosta.saladMan.entity.menu.MenuIngredient;
import com.kosta.saladMan.entity.menu.StoreMenu;
import com.kosta.saladMan.entity.menu.TotalMenu;
import com.kosta.saladMan.entity.store.Store;
import com.kosta.saladMan.repository.MenuRepository;
import com.kosta.saladMan.repository.StoreRepository;
import com.kosta.saladMan.repository.alarm.AlarmMsgRepository;
import com.kosta.saladMan.repository.inventory.CategoryRepository;
import com.kosta.saladMan.repository.inventory.IngredientRepository;
import com.kosta.saladMan.repository.menu.MenuCategoryRepository;
import com.kosta.saladMan.repository.menu.SMenuDslRepository;
import com.kosta.saladMan.repository.menu.StoreMenuRepository;
import com.kosta.saladMan.repository.saleOrder.SalesDslRepository;
import com.kosta.saladMan.repository.user.MenuIngredientRepository;
import com.kosta.saladMan.repository.user.TotalMenuRepository;
import com.kosta.saladMan.service.alarm.FcmMessageService;
import com.kosta.saladMan.util.PageInfo;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class StoreMenuServiceImpl implements StoreMenuService {
	
	private final MenuRepository menuRepository; // TotalMenu
	private final StoreRepository storeRepository; //Store
	private final StoreMenuRepository storeMenuRepository; //StoreMenu
	private final SMenuDslRepository sMenuDslRepository; //DSL
	private final IngredientRepository ingredientRepository;
	private final MenuCategoryRepository menuCategoryRepository;
	private final S3Uploader s3Uploader;
	private final CategoryRepository categoryRepository; //ingredientcategory
	private final TotalMenuRepository totalMenuRepository;
	private final MenuIngredientRepository menuIngredientRepository;
	
	//fcm알람
	private final AlarmMsgRepository alarmMsgRepository;
	private final FcmMessageService fcmMessageService;

	@Override
	public List<TotalMenuDto> getTotalMenu(PageInfo pageInfo, String sort, Integer categoryId) throws Exception {
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
		Page<TotalMenu> pages;
		
	    if (categoryId != null) {
	        // 카테고리별 메뉴만
	        pages = menuRepository.findByCategoryId(categoryId, pageRequest);
	    } else {
	        // 전체 메뉴
	        pages = menuRepository.findAll(pageRequest);
	    }
		
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
	public List<StoreMenuStatusDto> getMenuStatus(Integer storeId) throws Exception {
		return sMenuDslRepository.findMenuWithStoreStatus(storeId);
	}

	@Override
	public boolean toggleMenuStatus(Integer storeId, Integer menuId) throws Exception {
	    Optional<StoreMenu> optional = storeMenuRepository.findByStoreIdAndMenuId(storeId, menuId);

	    if (optional.isPresent()) {
	        StoreMenu storeMenu = optional.get();
	        boolean newStatus = !storeMenu.getStatus();
	        storeMenu.setStatus(newStatus);
	        storeMenuRepository.save(storeMenu);
	        return newStatus;
	    } else {
	        Store store = storeRepository.findById(storeId).orElseThrow();
	        TotalMenu menu = menuRepository.findById(menuId).orElseThrow();

	        StoreMenu newStoreMenu = new StoreMenu();
	        newStoreMenu.setStore(store);
	        newStoreMenu.setMenu(menu);
	        newStoreMenu.setStatus(true); // 최초 등록은 활성화

	        storeMenuRepository.save(newStoreMenu);
	        return true;
	    }
	}
	
	@Override
	public List<RecipeDto> getAllMenuRecipes(PageInfo pageInfo, Integer categoryId) throws Exception {
	    PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 12);

	    Page<TotalMenu> pages;
	    if (categoryId != null) {
	        pages = menuRepository.findByCategoryId(categoryId, pageRequest);
	    } else {
	        pages = menuRepository.findAll(pageRequest);
	    }

	    pageInfo.setAllPage(pages.getTotalPages());
	    int startPage = (pageInfo.getAllPage() - 1) / 10 * 10 + 1;
	    int endPage = Math.min(startPage + 9, pageInfo.getAllPage());
	    pageInfo.setStartPage(startPage);
	    pageInfo.setEndPage(endPage);

	    return sMenuDslRepository.findAllMenusWithIngredients(pageRequest, categoryId);
	}


	@Override
	public List<IngredientDto> getAllIngredients() throws Exception {
		return ingredientRepository.findAll()
                .stream()
                .map(Ingredient::toDto)
                .collect(Collectors.toList());
	}

	@Override
	public List<IngredientInfoDto> getIngredientInfo() throws Exception {
		return sMenuDslRepository.findIngredientsWithCategoryAndHqPrice();
	}

	@Override
	public List<MenuCategoryDto> getMenuCategory() throws Exception {
		return menuCategoryRepository.findAll()
				.stream()
		        .map(MenuCategory::toDto)
		        .collect(Collectors.toList());
	}

	@Override
	public void registerMenu(MenuRegisterDto dto, MultipartFile imageFile) {
		// 1. 이미지 저장
        String imageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
			try {
				imageUrl = s3Uploader.upload(imageFile, "menu-img");
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("이미지 저장 실패", e);
			}
			System.out.println(imageUrl);
        }
        MenuCategory category = menuCategoryRepository.findById(dto.getCategoryId())
        	    .orElseThrow(() -> new IllegalArgumentException("카테고리 없음"));
        
        String description = dto.getIngredients().stream()
        .map(i -> {
            Ingredient ing = ingredientRepository.findById(i.getIngredientId())
                    .orElseThrow(() -> new IllegalArgumentException("재료 없음"));
            return ing.getName();  // 이름만 추출
        })
        .collect(Collectors.joining(" + "));
        
        // 2. 메뉴 저장
        TotalMenu menu = TotalMenu.builder()
                .name(dto.getName())
                .salePrice(dto.getSalePrice())
                .category(category)
                .description(description)
                .img(imageUrl)
                .build();
        totalMenuRepository.save(menu);
        
        //alarm
        List<Store> storeList = storeRepository.findAll();
        
        for (Store store : storeList) {
        	SendAlarmDto alarmDto = SendAlarmDto.builder()
	                .storeId(store.getId())
	                .alarmMsgId(5) // 템플릿 ID만 넘김
	                .build();
            
            fcmMessageService.sendAlarm(alarmDto);
        }
        //

        // 3. 메뉴 재료 저장
        for (MenuIngredientDto ingDto : dto.getIngredients()) {
            Ingredient ingredient = ingredientRepository.findById(ingDto.getIngredientId()).orElseThrow();
            MenuIngredient menuIngredient = MenuIngredient.builder()
                    .menu(menu)
                    .ingredient(ingredient)
                    .quantity(ingDto.getQuantity())
                    .build();
            menuIngredientRepository.save(menuIngredient);
        }
    }
	
	//메뉴 품절처리
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void markSoldOut(Integer storeId, List<Integer> menuIds) throws Exception {
        int updatedCount = storeMenuRepository.markMenusSoldOut(storeId, menuIds);
	}
}
