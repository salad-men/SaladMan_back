package com.kosta.saladMan.repository;

import com.kosta.saladMan.entity.store.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Integer> {
}
