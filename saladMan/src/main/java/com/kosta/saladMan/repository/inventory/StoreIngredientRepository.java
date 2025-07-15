// StoreIngredientRepository.java
package com.kosta.saladMan.repository.inventory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

	List<StoreIngredient> findByStoreIdAndIngredientIdAndExpiredDateAfterAndQuantityGreaterThan(Integer storeId,
			Integer ingredientId, LocalDate expiredAfter, int quantityThreshold);

	@Query("SELECT SUM(si.quantity) FROM StoreIngredient si WHERE si.store.id = :storeId AND si.ingredient.id = :ingredientId AND si.expiredDate >= :today")
	Optional<Integer> sumAvailableQuantity(@Param("storeId") Integer storeId,
			@Param("ingredientId") Integer ingredientId, @Param("today") LocalDate today);

	@Query("SELECT s.ingredient.id, SUM(s.quantity) FROM StoreIngredient s "
			+ "WHERE s.store.id = :storeId AND s.expiredDate >= CURRENT_DATE " + "GROUP BY s.ingredient.id")
	List<Object[]> findValidIngredientQuantities(@Param("storeId") Integer storeId);

	@Query("SELECT si FROM StoreIngredient si WHERE si.store = :store AND si.ingredient.id = :ingredientId AND (si.expiredDate IS NULL OR si.expiredDate > :today)")
	List<StoreIngredient> findByStoreAndIngredientIdAndNotExpired(@Param("store") Store store,
			@Param("ingredientId") Integer ingredientId, @Param("today") LocalDate today);
}
