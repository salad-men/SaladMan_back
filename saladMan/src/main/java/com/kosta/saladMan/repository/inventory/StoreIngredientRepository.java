// StoreIngredientRepository.java
package com.kosta.saladMan.repository.inventory;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.entity.inventory.StoreIngredient;
import com.kosta.saladMan.entity.inventory.StoreIngredientSetting;
@Repository
public interface StoreIngredientRepository extends JpaRepository<StoreIngredient, Integer> {
	
}
