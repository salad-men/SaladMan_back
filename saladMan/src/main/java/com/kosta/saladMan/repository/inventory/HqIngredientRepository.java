package com.kosta.saladMan.repository.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.entity.inventory.HqIngredient;
import com.kosta.saladMan.entity.inventory.Ingredient;
import com.kosta.saladMan.entity.inventory.StoreIngredient;
import com.kosta.saladMan.entity.store.Store;
@Repository
public interface HqIngredientRepository extends JpaRepository<HqIngredient, Integer> {
}
