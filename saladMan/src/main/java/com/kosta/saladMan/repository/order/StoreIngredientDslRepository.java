package com.kosta.saladMan.repository.order;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.dto.purchaseOrder.LowStockItemDto;
import com.kosta.saladMan.entity.inventory.QIngredient;
import com.kosta.saladMan.entity.inventory.QStoreIngredient;
import com.kosta.saladMan.entity.inventory.QStoreIngredientSetting;
import com.kosta.saladMan.entity.purchaseOrder.QPurchaseOrder;
import com.kosta.saladMan.entity.purchaseOrder.QPurchaseOrderItem;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class StoreIngredientDslRepository {
	@Autowired
	private JPAQueryFactory jpaQueryFactory;

	@Autowired
	private ModelMapper modelMapper;

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

}
