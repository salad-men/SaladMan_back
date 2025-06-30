package com.kosta.saladMan.repository.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.dto.purchaseOrder.FixedOrderItemDto;
import com.kosta.saladMan.entity.inventory.QHqIngredient;
import com.kosta.saladMan.entity.inventory.QIngredient;
import com.kosta.saladMan.entity.inventory.QIngredientCategory;
import com.kosta.saladMan.entity.inventory.QStoreIngredientSetting;
import com.kosta.saladMan.entity.purchaseOrder.QFixedOrderItem;
import com.kosta.saladMan.entity.purchaseOrder.QFixedOrderTemplate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class FixedOrderDslRepository {
	@Autowired
	private JPAQueryFactory jpaQueryFactory;

	public List<FixedOrderItemDto> findAllByStore(Integer storeId) {
		QFixedOrderItem item = QFixedOrderItem.fixedOrderItem;
		QFixedOrderTemplate template = QFixedOrderTemplate.fixedOrderTemplate;
		QIngredient ingredient = QIngredient.ingredient;
		QIngredientCategory category = QIngredientCategory.ingredientCategory;
		QHqIngredient hqIngredient = QHqIngredient.hqIngredient;
		QStoreIngredientSetting setting = QStoreIngredientSetting.storeIngredientSetting;
		
		QFixedOrderItem itemSub = new QFixedOrderItem("itemSub");
		QFixedOrderTemplate templateSub = new QFixedOrderTemplate("templateSub");
		QHqIngredient hqSub = new QHqIngredient("hqSub");

		return jpaQueryFactory
		        .select(Projections.fields(
		            FixedOrderItemDto.class,
		            ingredient.id.as("ingredientId"),
		            ingredient.name.as("ingredientName"),
		            category.name.as("categoryName"),
		            ingredient.unit,

		            // min_quantity와 max_quantity를 집계
		            setting.minQuantity.max().as("minQuantity"),
		            setting.maxQuantity.max().as("maxQuantity"),

		            // 최신 FixedOrderItem autoOrderEnabled
		            com.querydsl.core.types.dsl.Expressions.as(
		                JPAExpressions
		                    .select(itemSub.autoOrderEnabled)
		                    .from(itemSub)
		                    .join(itemSub.fixedOrderTemplate, templateSub)
		                    .where(
		                        itemSub.ingredient.id.eq(ingredient.id)
		                            .and(templateSub.store.id.eq(storeId))
		                    )
		                    .orderBy(itemSub.id.desc())
		                    .limit(1),
		                "autoOrderEnabled"
		            ),

		            // 최신 FixedOrderItem autoOrderQty
		            com.querydsl.core.types.dsl.Expressions.as(
		                JPAExpressions
		                    .select(itemSub.autoOrderQty)
		                    .from(itemSub)
		                    .join(itemSub.fixedOrderTemplate, templateSub)
		                    .where(
		                        itemSub.ingredient.id.eq(ingredient.id)
		                            .and(templateSub.store.id.eq(storeId))
		                    )
		                    .orderBy(itemSub.id.desc())
		                    .limit(1),
		                "autoOrderQty"
		            ),

		            // 최신 HQIngredient minimumOrderUnit
		            com.querydsl.core.types.dsl.Expressions.as(
		                JPAExpressions
		                    .select(hqSub.minimumOrderUnit.max())
		                    .from(hqSub)
		                    .where(hqSub.ingredient.id.eq(ingredient.id))
		                    .orderBy(hqSub.receivedDate.desc(), hqSub.id.desc())
		                    ,
		                "minimumOrderUnit"
		            )
		        ))
		        .from(ingredient)
		        .leftJoin(ingredient.category, category)
		        .leftJoin(setting)
		            .on(
		                setting.ingredient.id.eq(ingredient.id)
		                    .and(setting.store.id.eq(storeId))
		            )
		        .groupBy(
		            ingredient.id,
		            ingredient.name,
		            category.name,
		            ingredient.unit
		        )
		        .orderBy(
		            category.name.asc(),
		            ingredient.name.asc()
		        )
		        .fetch();

	}
}
