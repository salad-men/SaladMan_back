package com.kosta.saladMan.repository.inventory;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.saladMan.entity.inventory.StoreIngredientStock;

public interface StoreIngredientStockRepository extends JpaRepository<StoreIngredientStock, Integer> {
	Optional<StoreIngredientStock> findFirstByPurchaseOrderIdAndIngredientId(Integer orderId, Integer ingredientId);
}
