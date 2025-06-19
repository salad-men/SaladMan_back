package com.kosta.saladMan.repository.inventory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.entity.inventory.InventoryRecord;

@Repository
public interface InventoryRecordRepository extends JpaRepository<InventoryRecord, Integer> {
    List<InventoryRecord> findByStoreIdAndChangeType(Integer storeId, String changeType);

}
