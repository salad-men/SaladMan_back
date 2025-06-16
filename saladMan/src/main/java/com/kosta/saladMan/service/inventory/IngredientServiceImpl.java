//package com.kosta.saladMan.service.inventory;
//
//import java.util.List;
//
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Service;
//
//import com.kosta.saladMan.dto.inventory.IngredientDto;
//import com.kosta.saladMan.entity.inventory.Ingredient;
//import com.kosta.saladMan.repository.inventory.IngredientCategoryRepository;
//import com.kosta.saladMan.repository.inventory.IngredientRepository;
//import com.kosta.saladMan.util.PageInfo;
//
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//public class IngredientServiceImpl implements IngredientService {
//	
//	private final IngredientCategoryRepository ingredientCategoryRepository;
//    private final IngredientRepository ingredientRepository;
//
//    
//	@Override
//	public List<String> getAllCategories() {
//	
//    return List.of("베이스채소", "단백질", "토핑", "드레싱");
//
//    //return ingredientCategoryRepository.findAllCategoryNames();
//
//	}
//
//	@Override
//	public List<String> getAllStores() {
//        return List.of("본사", "강남점", "홍대점", "건대점");
//        //추후 StoreRepository에서 가져오기
//	}
//
//	 @Override
//	    public List<IngredientDto> searchIngredientList(PageInfo pageInfo, String store, String category, String name) throws Exception {
//	        PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 10, Sort.by(Sort.Direction.DESC, "id"));
//	        Page<Ingredient> pages = null;
//
//	        boolean isSearchActive = (store != null && !store.equalsIgnoreCase("all")) ||
//	                                 (category != null && !category.equalsIgnoreCase("all")) ||
//	                                 (name != null && !name.trim().isEmpty());
//	        
//	        if (!isSearchActive) {
//	            pages = ingredientRepository.findAll(pageRequest);
//	        } else {
//	            pages = ingredientRepository.findByStoreContainingAndCategoryContainingAndNameContainingIgnoreCase(
//	                    store == null ? "" : store,
//	                    category == null ? "" : category,
//	                    name == null ? "" : name,
//	                    pageRequest);
//	        }
//
//	        pageInfo.setAllPage(pages.getTotalPages());
//	        Integer startPage = (pageInfo.getAllPage() - 1) / 10 * 10 + 1;
//	        Integer endPage = Math.min(startPage + 10 - 1, pageInfo.getAllPage());
//	        pageInfo.setStartPage(startPage);
//	        pageInfo.setEndPage(endPage);
//
//	        return pages.getContent().stream().map(IngredientDto::fromEntity).collect(Collectors.toList());
//	    }
//}
