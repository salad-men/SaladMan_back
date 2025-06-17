package com.kosta.saladMan.service.user;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.kosta.saladMan.dto.menu.MenuIngredientViewDto;
import com.kosta.saladMan.dto.menu.TotalMenuDto;
import com.kosta.saladMan.entity.menu.MenuIngredient;
import com.kosta.saladMan.entity.menu.TotalMenu;
import com.kosta.saladMan.repository.MenuRepository;
import com.kosta.saladMan.repository.user.MenuIngredientRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final MenuIngredientRepository menuIngredientRepository;

    @Override
    public List<TotalMenuDto> getMenusByCategory(Integer categoryId) {
        List<TotalMenu> menus = menuRepository.findByCategoryId(categoryId);

        return menus.stream().map(menu -> {
            TotalMenuDto dto = TotalMenuDto.fromEntity(menu);

            List<MenuIngredient> ingredients = menuIngredientRepository.findByMenuId(menu.getId());

            List<MenuIngredientViewDto> ingredientDtos = ingredients.stream()
                .map(mi -> MenuIngredientViewDto.builder()
                    .name(mi.getIngredient().getName())
                    .quantity(mi.getQuantity())
                    .build())
                .collect(Collectors.toList());

            dto.setIngredients(ingredientDtos);
            return dto;
        }).collect(Collectors.toList());
    }
}




//package com.kosta.saladMan.service.user;
//
//import com.kosta.saladMan.dto.menu.TotalMenuDto;
//import com.kosta.saladMan.entity.menu.TotalMenu;
//import com.kosta.saladMan.repository.MenuRepository;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class MenuServiceImpl implements MenuService {
//
//    private final MenuRepository menuRepository;
//
//    @Override
//    public List<TotalMenuDto> getMenusByCategory(Integer categoryId) {
//        List<TotalMenu> menus = menuRepository.findByCategoryId(categoryId);
//        return menus.stream()
//                .map(TotalMenuDto::fromEntity)
//                .collect(Collectors.toList());
//    }
//}



