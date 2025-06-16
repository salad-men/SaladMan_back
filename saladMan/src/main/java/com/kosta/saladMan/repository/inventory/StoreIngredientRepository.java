// StoreIngredientRepository.java
package com.kosta.saladMan.repository.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kosta.saladMan.entity.inventory.StoreIngredient;

public interface StoreIngredientRepository extends JpaRepository<StoreIngredient, Integer> {
}
