package com.kosta.saladMan.repository.inventory;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.entity.inventory.Ingredient;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {
	
	
}
