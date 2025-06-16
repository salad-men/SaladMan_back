package com.kosta.saladMan.repository;

import com.kosta.saladMan.entity.store.Store;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Integer> {
	 Page<Store> findByRoleNot(String role, Pageable pageable);
}
