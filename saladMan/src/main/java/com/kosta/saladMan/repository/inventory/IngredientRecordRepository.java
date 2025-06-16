// IngredientRecordRepository.java
package com.kosta.saladMan.repository.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kosta.saladMan.entity.inventory.IngredientRecord;

public interface IngredientRecordRepository extends JpaRepository<IngredientRecord, Integer> {
}
