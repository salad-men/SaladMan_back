// IngredientRecordRepository.java
package com.kosta.saladMan.repository.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.entity.inventory.IngredientRecord;
@Repository
public interface IngredientRecordRepository extends JpaRepository<IngredientRecord, Integer> {
}
