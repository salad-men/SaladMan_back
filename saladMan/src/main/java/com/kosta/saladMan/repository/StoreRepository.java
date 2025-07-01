package com.kosta.saladMan.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.kosta.saladMan.entity.store.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Repository
public interface StoreRepository extends JpaRepository<Store, Integer> {
	
	Page<Store> findByRoleNot(String role, Pageable pageable);
	Optional<Store> findByUsername(String username);
	Optional<Store> findByName(String name);
	@Query("SELECT DISTINCT s.location FROM Store s")
	List<String> findDistinctLocations();
	@Query("SELECT s.name FROM Store s")
	List<String> findAllStoreNames();
	
	@Transactional
	@Modifying
	@Query("update Store s set s.fcmToken=:fcmToken where s.username=:username")
	void updateFcmToken(@Param("username")String username, @Param("fcmToken")String fcmToken);
	
	Optional<Store> findById(Long id);
	
	List<Store> findByAutoOrderEnabledTrue();
}
