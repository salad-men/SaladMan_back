package com.kosta.saladMan.repository.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kosta.saladMan.entity.store.Store;

public interface StoreChatBotRepository extends JpaRepository<Store, Integer> {
    List<Store> findByAddressContainingIgnoreCase(String keyword);
    List<Store> findByNameContainingOrAddressContaining(String name, String address);
    
    @Query("SELECT s FROM Store s WHERE s.name LIKE %:keyword% OR s.address LIKE %:keyword%")
    List<Store> findByKeyword(@Param("keyword") String keyword);
}