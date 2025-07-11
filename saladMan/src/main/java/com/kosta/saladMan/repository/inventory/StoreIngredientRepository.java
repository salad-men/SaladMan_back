// StoreIngredientRepository.java
package com.kosta.saladMan.repository.inventory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.entity.inventory.Ingredient;
import com.kosta.saladMan.entity.inventory.StoreIngredient;
import com.kosta.saladMan.entity.inventory.StoreIngredientSetting;
import com.kosta.saladMan.entity.store.Store;
@Repository
public interface StoreIngredientRepository extends JpaRepository<StoreIngredient, Integer> {
	Optional<StoreIngredient> findByStoreAndIngredient(Store store, Ingredient ingredient);
    List<StoreIngredient> findByStoreAndIngredientId(Store store, Integer ingredientId);
    StoreIngredient findByStoreIdAndIngredientId(Integer storeId, Integer ingredientId);
    List<StoreIngredient> findByStoreIdAndIngredientIdAndExpiredDateAfterAndQuantityGreaterThan(
    	    Integer storeId,
    	    Integer ingredientId,
    	    LocalDate expiredAfter,
    	    int quantityThreshold
    	);
}
