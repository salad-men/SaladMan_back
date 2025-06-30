package com.kosta.saladMan.repository.order;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.dto.inventory.HqIngredientDto;
import com.kosta.saladMan.dto.purchaseOrder.PurchaseOrderDto;
import com.kosta.saladMan.dto.purchaseOrder.PurchaseOrderItemDto;
import com.kosta.saladMan.entity.inventory.QHqIngredient;
import com.kosta.saladMan.entity.inventory.QIngredient;
import com.kosta.saladMan.entity.inventory.QIngredientCategory;
import com.kosta.saladMan.entity.purchaseOrder.QPurchaseOrder;
import com.kosta.saladMan.entity.purchaseOrder.QPurchaseOrderItem;
import com.kosta.saladMan.entity.store.QStore;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class PuchaseOrderDslRepository {
	@Autowired
	private JPAQueryFactory jpaQueryFactory;

	@Autowired
	private ModelMapper modelMapper;

	// 본사 발주 신청 목록
	public Page<PurchaseOrderDto> findOrderApplyList(String storeName, String status, LocalDate startDate,
			LocalDate endDate, Pageable pageable) {

		QPurchaseOrder po = QPurchaseOrder.purchaseOrder;
		QStore store = QStore.store;
		QPurchaseOrderItem poi = QPurchaseOrderItem.purchaseOrderItem;
		QIngredient i = QIngredient.ingredient;

		BooleanBuilder builder = new BooleanBuilder();

		if (storeName != null && !storeName.trim().isEmpty()) {
			builder.and(store.name.containsIgnoreCase(storeName));
		}

		if (status != null && !status.trim().isEmpty() && !status.equals("전체")) {
			builder.and(po.status.eq(status));
		}

		if (startDate != null && endDate != null) {
			builder.and(po.orderDateTime.between(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)));
		}

		JPQLQuery<Long> receivedCount = JPAExpressions.select(poi.count()).from(poi)
				.where(poi.purchaseOrder.id.eq(po.id).and(poi.receivedQuantity.gt(0)));

		JPQLQuery<Long> totalCount = JPAExpressions.select(poi.count()).from(poi).where(poi.purchaseOrder.id.eq(po.id));

		Expression<String> quantityExpr = Expressions.stringTemplate("concat({0}, '/', {1})", receivedCount,
				totalCount);

		List<PurchaseOrderDto> contents = jpaQueryFactory
				.select(Projections.constructor(PurchaseOrderDto.class, po.id, store.id, po.orderDateTime, po.status,
						po.requestedBy, po.totalPrice, po.purType, po.qrImg,po.orderStatus,
						ExpressionUtils.as(JPAExpressions
								.select(i.name.concat(" 외 ").concat(poi.count().subtract(1).stringValue()).concat("건"))
								.from(poi).join(poi.ingredient, i).where(poi.purchaseOrder.id.eq(po.id)),
								"productNameSummary"),
						ExpressionUtils.as(quantityExpr, "quantitySummary"),
						ExpressionUtils.as(po.status.in("입고완료", "검수완료"), "receiptAvailable"), store.name))
				.from(po).join(po.store, store).where(builder).offset(pageable.getOffset())
				.limit(pageable.getPageSize()).orderBy(po.orderDateTime.desc()).fetch();

		long total = jpaQueryFactory.select(po.count()).from(po).join(po.store, store).where(builder).fetchOne();

		return new PageImpl<>(contents, pageable, total);
	}
	
	//본사 발주 상세
	public List<PurchaseOrderItemDto> orderApplyDetail(Integer purchaseOrderId) {
		QPurchaseOrderItem poi = QPurchaseOrderItem.purchaseOrderItem;
		QPurchaseOrder po = QPurchaseOrder.purchaseOrder;
		QIngredient ingredient = QIngredient.ingredient;
		QIngredientCategory ingredientCategory = QIngredientCategory.ingredientCategory;
		QHqIngredient hIngredient = QHqIngredient.hqIngredient;
		QStore store = QStore.store;
		return jpaQueryFactory
					.select(Projections.fields(PurchaseOrderItemDto.class, 
							poi.id,
							poi.purchaseOrder.id.as("purchaseOrderId"),
							poi.ingredient.id.as("ingredientId"),
							poi.orderedQuantity,
							poi.receivedQuantity,
							poi.totalPrice,
							poi.approvalStatus,
							poi.rejectionReason,
							poi.ingredient.name.as("ingredientName"),
							ingredientCategory.name.as("categoryName"),
							poi.ingredient.unit,
							po.status.as("orderStatus"),
					        store.name.as("storeName")  // ✅ 추가

					))
					.from(poi)
					.join(poi.ingredient,ingredient)
					.join(ingredientCategory).on(poi.ingredient.category.id.eq(ingredientCategory.id))
					.join(po).on(poi.purchaseOrder.id.eq(po.id))
				    .join(po.store, store)
					.where(poi.purchaseOrder.id.eq(purchaseOrderId))
					.fetch();
		
	}
	
	public List<HqIngredientDto> findAvailableStockByIngredientId(Integer ingredientId,Integer storeId, LocalDate limitDate){
		QHqIngredient hIng = QHqIngredient.hqIngredient;
		QIngredient ing = QIngredient.ingredient;
		QStore store = QStore.store;
		
		return jpaQueryFactory
		        .select(Projections.fields(HqIngredientDto.class,
		        		hIng.id,
		        		hIng.ingredient.id.as("ingredientId"),
		        		hIng.ingredient.name.as("ingredientName"),
		        		hIng.unitCost,
		        		hIng.quantity,
		        		hIng.reservedQuantity,
		        		hIng.expiredDate,
		        		hIng.receivedDate,
		        		store.name.as("storeName"),
		        		ing.unit.as("unit")
		        		
		        ))
		        .from(hIng)
		        .join(hIng.store, store)
		        .join(ing).on(hIng.ingredient.id.eq(ing.id))
		        .where(
		        		hIng.ingredient.id.eq(ingredientId),
		        		hIng.quantity.gt(0),
		        		hIng.expiredDate.goe(limitDate)  // 핵심 조건
		        )
		        .orderBy(hIng.expiredDate.asc())
		        .fetch();
		
	}

	
	// 매장--------------------------
	// 매장 발주 목록 조회
	public Page<PurchaseOrderDto> findPagedOrders(Integer id, String orderType, String productName, LocalDate startDate,
			LocalDate endDate, Pageable pageable) {
		QPurchaseOrder po = QPurchaseOrder.purchaseOrder;
		QPurchaseOrderItem poi = QPurchaseOrderItem.purchaseOrderItem;
		QIngredient i = QIngredient.ingredient;

		int page = pageable.getPageNumber();
		int size = pageable.getPageSize();

		// 공통 where 조건
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(po.store.id.eq(id));

		if (orderType != null && !orderType.isBlank()) {
			builder.and(po.purType.eq(orderType));
		}
		if (startDate != null) {
			builder.and(po.orderDateTime.goe(startDate.atStartOfDay()));
		}
		if (endDate != null) {
			builder.and(po.orderDateTime.loe(endDate.atTime(23, 59, 59)));
		}
		if (productName != null && !productName.isBlank()) {
			builder.and(po.id.in(JPAExpressions.select(poi.purchaseOrder.id).from(poi).join(poi.ingredient, i)
					.where(i.name.containsIgnoreCase(productName))));
		}

		JPQLQuery<Long> receivedCount = JPAExpressions.select(poi.count()).from(poi)
				.where(poi.purchaseOrder.id.eq(po.id).and(poi.receivedQuantity.gt(0)));

		// 전체 품목 수
		JPQLQuery<Long> totalCount = JPAExpressions.select(poi.count()).from(poi).where(poi.purchaseOrder.id.eq(po.id));

		Expression<String> quantityExpr = Expressions.stringTemplate("concat({0}, '/', {1})", receivedCount,
				totalCount);

		JPQLQuery<PurchaseOrderDto> query = jpaQueryFactory
				.select(Projections.bean(PurchaseOrderDto.class, po.id, po.store.id.as("storeId"), po.orderDateTime,
						po.status, po.requestedBy, po.totalPrice, po.purType, po.qrImg,
						// 품명 요약
						ExpressionUtils.as(JPAExpressions
								.select(i.name.concat(" 외 ").concat(poi.count().subtract(1).stringValue()).concat("건"))
								.from(poi).join(poi.ingredient, i).where(poi.purchaseOrder.id.eq(po.id)),
								"productNameSummary"),
						// 입고량 표시
						ExpressionUtils.as(quantityExpr, "quantitySummary"),

						// 입고검수서 노출 조건
						ExpressionUtils.as(po.status.in("입고완료", "검수완료"), "receiptAvailable")))
				.from(po).where(builder).orderBy(po.orderDateTime.desc()).offset(page * size).limit(size);

		List<PurchaseOrderDto> content = query.fetch();

		// count 쿼리 (서브쿼리 조건 포함)
		long total = jpaQueryFactory.select(po.count()).from(po).where(builder).fetchOne();

		return new PageImpl<>(content, pageable, total);
	}
	
	public List<PurchaseOrderItemDto> getInspectionInfo(Integer orderId){
		QPurchaseOrderItem poi = QPurchaseOrderItem.purchaseOrderItem;
	    QPurchaseOrder po = QPurchaseOrder.purchaseOrder;
		QHqIngredient hqIngredient = QHqIngredient.hqIngredient;
		QIngredient ingredient = QIngredient.ingredient;
	    QIngredientCategory category = QIngredientCategory.ingredientCategory;
	    QPurchaseOrder order = QPurchaseOrder.purchaseOrder;
	    QStore store = QStore.store;
	    
	    return jpaQueryFactory
	            .select(Projections.fields(PurchaseOrderItemDto.class,
	                poi.id,
	                po.id.as("purchaseOrderId"),
	                ingredient.id.as("ingredientId"),
	                ingredient.name.as("ingredientName"),
	                ingredient.unit.as("unit"),
	                category.name.as("categoryName"),
	                poi.orderedQuantity,
	                poi.receivedQuantity,
	                poi.totalPrice,
	                poi.inspection,
	                poi.inspectionNote,
	                poi.approvalStatus,
	                poi.rejectionReason,
	                store.name.as("storeName"),
	                hqIngredient.unitCost.max().as("unitCost"), // 가장 큰 날짜 = 가장 최근 단가
	                po.status.as("orderStatus"),
	                po.orderDateTime.as("orderDateTime")
	            ))
	            .from(poi)
	            .leftJoin(poi.ingredient, ingredient)
	            .leftJoin(ingredient.category, category)
	            .leftJoin(poi.purchaseOrder, po)
	            .leftJoin(po.store, store)
	            .leftJoin(hqIngredient).on(hqIngredient.ingredient.id.eq(ingredient.id))
	            .where(po.id.eq(orderId))
	            .groupBy(poi.id) //item 기준 group by
	            .fetch();
	}

}
