package com.kosta.saladMan.repository.order;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.dto.purchaseOrder.LowStockItemDto;
import com.kosta.saladMan.dto.purchaseOrder.StoreOrderItemDto;
import com.kosta.saladMan.entity.inventory.QHqIngredient;
import com.kosta.saladMan.entity.inventory.QIngredient;
import com.kosta.saladMan.entity.inventory.QStoreIngredient;
import com.kosta.saladMan.entity.inventory.QStoreIngredientSetting;
import com.kosta.saladMan.entity.purchaseOrder.QPurchaseOrder;
import com.kosta.saladMan.entity.purchaseOrder.QPurchaseOrderItem;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class StoreIngredientDslRepository {
	@Autowired
	private JPAQueryFactory jpaQueryFactory;

	@Autowired
	private ModelMapper modelMapper;

	//최소수량 미달 재료 
	public List<LowStockItemDto> findLowStockIngredientsByStore(Integer id) {
		QStoreIngredientSetting qsetting = QStoreIngredientSetting.storeIngredientSetting;
		QStoreIngredient qstoreIngre = QStoreIngredient.storeIngredient;
		QPurchaseOrder qpo = QPurchaseOrder.purchaseOrder;
		QPurchaseOrderItem qpi = QPurchaseOrderItem.purchaseOrderItem;
		QIngredient qin = QIngredient.ingredient;

		return jpaQueryFactory
				.select(Projections.constructor(LowStockItemDto.class, qsetting.ingredient.name,
						qsetting.ingredient.category.name, qstoreIngre.quantity.coalesce(0),qin.unit))
				.from(qsetting)
				.leftJoin(qstoreIngre)
				.on(qsetting.store.id.eq(qstoreIngre.store.id), qsetting.ingredient.id.eq(qstoreIngre.ingredient.id))
				.leftJoin(qin).on(qsetting.ingredient.id.eq(qin.ingredient.id))
				.where(
						qsetting.store.id.eq(id), 
						qsetting.minQuantity.gt(qstoreIngre.quantity.coalesce(0)
						.add(
								JPAExpressions
								.select(
										qpi.orderedQuantity.subtract(qpi.receivedQuantity).sum().coalesce(0))
								.from(qpi).join(qpi.purchaseOrder, qpo).where(qpo.store.id.eq(id),
										qpi.ingredient.id.eq(qsetting.ingredient.id), qpo.status.in("대기중") // ←
																													// 입고
																													// 전
																													// 상태만
								)))).fetch();

	}
	
	//발주 신청을 위한 재료 리스트 
	public List<StoreOrderItemDto> findAvailableOrderItemsByStore(Integer id,String category,String keyword){
		QIngredient ing = QIngredient.ingredient;
		QHqIngredient hq = QHqIngredient.hqIngredient;
		QStoreIngredient si = QStoreIngredient.storeIngredient;
		QPurchaseOrder po = QPurchaseOrder.purchaseOrder;
		QPurchaseOrderItem poi = QPurchaseOrderItem.purchaseOrderItem;

		return jpaQueryFactory
			    .select(Projections.constructor(
			        StoreOrderItemDto.class,
			        ing.id,
			        ing.name,
			        ing.category.name,
			        si.quantity.coalesce(0),
			        JPAExpressions.select(poi.orderedQuantity.sum().coalesce(0))
			            .from(poi)
			            .join(poi.purchaseOrder, po)
			            .where(
			                poi.ingredient.id.eq(ing.id),
			                po.store.id.eq(id),
			                po.status.in("대기중", "승인됨")
			            ),
			        ing.unit,
			        hq.unitCost.coalesce(0),           // max 단가로 대표값 처리
			        hq.minimumOrderUnit.coalesce(0),    // 여러 개일 경우도 방지
			        hq.quantity.coalesce(0),
			        ing.available
			    ))
			    .from(ing)
			    .leftJoin(si).on(
			        si.store.id.eq(id),
			        si.ingredient.id.eq(ing.id)
			    )
			    .leftJoin(hq).on(
			        hq.ingredient.id.eq(ing.id)
			    )
			    .where(
			        category != null && !category.equals("전체")
			            ? ing.category.name.eq(category) : null,
			        keyword != null && !keyword.isBlank()
			            ? ing.name.containsIgnoreCase(keyword) : null
			    )
			    .groupBy(
			        ing.id,
			        ing.name,
			        ing.category.name,
			        si.quantity,
			        ing.unit
			    )
			    .fetch();
	}

}
