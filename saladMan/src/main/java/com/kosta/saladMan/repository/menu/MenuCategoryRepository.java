package com.kosta.saladMan.repository.menu;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.dto.menu.MenuCategoryDto;
import com.kosta.saladMan.entity.menu.MenuCategory;
import com.kosta.saladMan.entity.menu.StoreMenu;

@Repository
public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Integer>{
	List<MenuCategoryDto> findAllByOrderByIdAsc();

}
