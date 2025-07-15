package com.kosta.saladMan.repository.inventory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.entity.inventory.Ingredient;
import com.kosta.saladMan.entity.inventory.InventoryRecord;
import com.kosta.saladMan.entity.store.Store;

@Repository
public interface InventoryRecordRepository extends JpaRepository<InventoryRecord, Integer> {
	
	@Query("SELECT r FROM InventoryRecord r " + "WHERE r.ingredient = :ingredient " + "AND r.store = :store "
			+ "AND DATE(r.date) = :today " + "AND r.changeType = '출고' " + "AND r.memo = '매장 판매'")
	Optional<InventoryRecord> findTodayOutRecord(@Param("ingredient") Ingredient ingredient,
			@Param("store") Store store, @Param("today") java.sql.Date today);

	List<InventoryRecord> findByStoreIdAndChangeType(Integer storeId, String changeType);

	Page<InventoryRecord> findByStoreIdAndChangeTypeOrderByDateDesc(Integer storeId, String changeType,
			Pageable pageable);
}
