package com.kosta.saladMan.repository.inventory;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.saladMan.entity.inventory.IngredientCategory;

public interface CategoryRepository extends JpaRepository<IngredientCategory, Integer> {

}
