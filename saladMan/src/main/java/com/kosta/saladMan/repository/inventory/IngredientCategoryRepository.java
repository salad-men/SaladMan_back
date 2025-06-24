// IngredientCategoryRepository.java
package com.kosta.saladMan.repository.inventory;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.entity.inventory.IngredientCategory;
@Repository
public interface IngredientCategoryRepository extends JpaRepository<IngredientCategory, Integer> {
    Optional<IngredientCategory> findByName(String name);
}
