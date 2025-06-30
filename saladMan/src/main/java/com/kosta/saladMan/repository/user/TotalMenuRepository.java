package com.kosta.saladMan.repository.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.saladMan.entity.menu.TotalMenu;

public interface TotalMenuRepository extends JpaRepository<TotalMenu, Long> {
    List<TotalMenu> findByCategoryId(Integer categoryId);
}