// StoreIngredientRepository.java
package com.kosta.saladMan.repository.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.entity.inventory.StoreIngredient;
@Repository
public interface StoreIngredientRepository extends JpaRepository<StoreIngredient, Integer> {
}
