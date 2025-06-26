package com.kosta.saladMan.repository.storeInquiry;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kosta.saladMan.entity.store.Store;

public interface StoreInquiryRepository extends JpaRepository<Store, String> {
	
	@Query("SELECT s FROM Store s WHERE s.id <> :storeId")
    List<Store> findOtherStores(@Param("storeId") Integer storeId);

}
