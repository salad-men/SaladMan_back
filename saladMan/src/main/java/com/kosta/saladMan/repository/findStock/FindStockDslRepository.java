package com.kosta.saladMan.repository.findStock;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.dto.storeInquiry.NearbyInventoryDto;
import com.kosta.saladMan.dto.storeInquiry.StoreInventoryResponseDto;
import com.kosta.saladMan.entity.inventory.QHqIngredient;
import com.kosta.saladMan.entity.inventory.QIngredient;
import com.kosta.saladMan.entity.inventory.QStoreIngredient;
import com.kosta.saladMan.entity.inventory.QStoreIngredientSetting;
import com.kosta.saladMan.entity.store.QStore;
import com.kosta.saladMan.entity.store.Store;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
@Repository
public class FindStockDslRepository {

	@Autowired
    private JPAQueryFactory queryFactory;
	
	 public List<NearbyInventoryDto> findInventoryByStoreIds(List<Integer> storeIds) {
	        if (storeIds.isEmpty()) return List.of(); // fallback: 빈 리스트 방지

	        QStore store = QStore.store;
	        QIngredient ingredient = QIngredient.ingredient;
	        QStoreIngredient storeIngredient = QStoreIngredient.storeIngredient;

	        return queryFactory
	                .select(Projections.constructor(
	                        NearbyInventoryDto.class,
	                        store.name,
	                        ingredient.name,
	                        ingredient.unit,
	                        store.id,
	                        ingredient.id,
	                        storeIngredient.quantity.sum(),
	                        store.latitude,
	                        store.longitude
	                ))
	                .from(storeIngredient)
	                .join(storeIngredient.store, store)
	                .join(storeIngredient.ingredient, ingredient)
	                .where(store.id.in(storeIds))
	                .groupBy(store.id, ingredient.id)
	                .fetch();
	    }
	
}
