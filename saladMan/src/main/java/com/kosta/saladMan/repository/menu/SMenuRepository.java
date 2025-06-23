package com.kosta.saladMan.repository.menu;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.saladMan.dto.menu.RecipeDto;
import com.kosta.saladMan.entity.menu.StoreMenu;

public interface SMenuRepository extends JpaRepository<StoreMenu, Integer> {
	Optional<StoreMenu> findByStoreIdAndMenuId(Integer storeId, Integer menuId);
}
