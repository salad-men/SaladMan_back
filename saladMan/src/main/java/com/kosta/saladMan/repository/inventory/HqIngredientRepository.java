package com.kosta.saladMan.repository.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.entity.inventory.HqIngredient;
@Repository
public interface HqIngredientRepository extends JpaRepository<HqIngredient, Integer> {
}
