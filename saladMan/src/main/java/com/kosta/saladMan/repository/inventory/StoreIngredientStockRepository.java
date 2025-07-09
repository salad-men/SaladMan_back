package com.kosta.saladMan.repository.inventory;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kosta.saladMan.entity.inventory.StoreIngredientStock;

public interface StoreIngredientStockRepository extends JpaRepository<StoreIngredientStock, Integer> {
	Optional<StoreIngredientStock> findFirstByPurchaseOrderIdAndIngredientId(Integer orderId, Integer ingredientId);
	List<StoreIngredientStock> findByPurchaseOrderIdAndIngredientId(Integer orderId, Integer ingredientId);
	@Query("SELECT SUM(s.quantity) FROM StoreIngredientStock s WHERE s.store.id = :storeId AND s.ingredient.id = :ingredientId AND s.receivedDate IS NULL")
	Optional<Integer> findIncomingQuantityByStoreAndIngredient(@Param("storeId") Integer storeId, @Param("ingredientId") Integer ingredientId);


}
