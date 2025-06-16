package com.kosta.saladMan.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.kosta.saladMan.entity.store.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface StoreRepository extends JpaRepository<Store, Integer> {
	
	Page<Store> findByRoleNot(String role, Pageable pageable);
	Optional<Store> findByUsername(String username);
	Optional<Store> findByName(String name);
	
}
