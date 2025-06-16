package com.kosta.saladMan.repository.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kosta.saladMan.entity.inventory.HqIngredient;

public interface HqIngredientRepository extends JpaRepository<HqIngredient, Integer> {
}
