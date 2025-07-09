package com.kosta.saladMan.repository.inventory;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.entity.inventory.Ingredient;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {
    Optional<Ingredient> findByNameAndCategoryId(String name, Integer categoryId);

    void deleteByCategoryId(@Param("categoryId") Integer categoryId);
}
