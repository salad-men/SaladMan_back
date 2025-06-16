package com.kosta.saladMan.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.entity.store.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, Integer> {
	
	Optional<Store> findByUsername(String username);
	Optional<Store> findByName(String sname);
}
