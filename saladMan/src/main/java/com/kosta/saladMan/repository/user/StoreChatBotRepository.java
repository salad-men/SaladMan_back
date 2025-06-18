package com.kosta.saladMan.repository.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.saladMan.entity.store.Store;

public interface StoreChatBotRepository extends JpaRepository<Store, Long> {
    List<Store> findByAddressContainingIgnoreCase(String keyword);
}