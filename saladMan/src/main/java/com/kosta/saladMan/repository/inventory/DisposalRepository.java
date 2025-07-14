package com.kosta.saladMan.repository.inventory;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.entity.inventory.Disposal;
@Repository
public interface DisposalRepository extends JpaRepository<Disposal, Integer> {

	int countByStoreIdAndStatusAndRequestedAtBetween(
		    Integer storeId,
		    String status,
	        LocalDate startDate,
	        LocalDate endDate
		);
	
}
