package com.kosta.saladMan.repository.inventory;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.entity.inventory.Ingredient;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {
	

	
	
	
}
