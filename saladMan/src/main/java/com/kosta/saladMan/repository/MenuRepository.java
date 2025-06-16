package com.kosta.saladMan.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.saladMan.entity.menu.TotalMenu;

public interface MenuRepository extends JpaRepository<TotalMenu, Integer>{

}
