package com.kosta.saladMan.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.saladMan.entity.menu.StoreMenu;
import com.kosta.saladMan.entity.menu.TotalMenu;

public interface MenuRepository extends JpaRepository<TotalMenu, Integer>{
    List<TotalMenu> findByCategoryId(Integer categoryId);
//    List<StoreMenu> findByIdAndStatusTrue(Integer storeId);

	Page<TotalMenu> findByCategoryId(Integer categoryId, Pageable pageable);
}
