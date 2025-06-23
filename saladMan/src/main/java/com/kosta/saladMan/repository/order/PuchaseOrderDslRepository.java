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

import com.kosta.saladMan.dto.purchaseOrder.PurchaseOrderDto;
import com.kosta.saladMan.entity.inventory.QIngredient;
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

	public Page<PurchaseOrderDto> findOrderApplyList(String storeName, String status, String approval,
			LocalDate startDate, LocalDate endDate, Pageable pageable) {

		QPurchaseOrder po = QPurchaseOrder.purchaseOrder;
		QStore store = QStore.store;
		QPurchaseOrderItem poi = QPurchaseOrderItem.purchaseOrderItem;
		QIngredient i = QIngredient.ingredient;

		BooleanBuilder builder = new BooleanBuilder();

		// ✅ 조건 1: storeName 검색
		if (storeName != null && !storeName.trim().isEmpty()) {
			builder.and(store.name.containsIgnoreCase(storeName));
		}

		// ✅ 조건 2: status (전체 제외)
		if (status != null && !status.equals("전체")) {
			builder.and(po.status.eq(status));
		}

//		// ✅ 조건 3: approval 상태 (전체 제외)
//		if (approval != null && !approval.equals("전체")) {
//			builder.and(purchaseOrder.approval.eq(approval));
//		}

		// ✅ 조건 4: 기간
		if (startDate != null && endDate != null) {
			builder.and(po.orderDateTime.between(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)));
		}
		
		JPQLQuery<Long> receivedCount = JPAExpressions.select(poi.count()).from(poi)
				.where(poi.purchaseOrder.id.eq(po.id).and(poi.receivedQuantity.gt(0)));

		// 전체 품목 수
		JPQLQuery<Long> totalCount = JPAExpressions.select(poi.count()).from(poi).where(poi.purchaseOrder.id.eq(po.id));

		Expression<String> quantityExpr = Expressions.stringTemplate("concat({0}, '/', {1})", receivedCount,
				totalCount);

		// ✅ 본문 쿼리
		List<PurchaseOrderDto> contents = jpaQueryFactory.select(Projections.constructor(PurchaseOrderDto.class,
				po.id, store.id, po.orderDateTime, po.status,
				po.requestedBy, po.totalPrice, po.purType, po.qrImg,
				// ↓ 아래 3개는 서브쿼리 또는 별도 처리 필요 (지금은 null로)
				ExpressionUtils.as(JPAExpressions
						.select(i.name.concat(" 외 ").concat(poi.count().subtract(1).stringValue()).concat("건"))
						.from(poi).join(poi.ingredient, i).where(poi.purchaseOrder.id.eq(po.id)),
						"productNameSummary"),
				// 입고량 표시
				ExpressionUtils.as(quantityExpr, "quantitySummary"),

				// 입고검수서 노출 조건
				ExpressionUtils.as(po.status.in("입고완료", "검수완료"), "receiptAvailable"),
				store.name // ✅ 조인된 매장명
		)).from(po).join(po.store, store).where(builder).offset(pageable.getOffset())
				.limit(pageable.getPageSize()).orderBy(po.orderDateTime.desc()).fetch();

		// ✅ count 쿼리
		long total = jpaQueryFactory.select(po.count()).from(po).join(po.store, store)
				.where(builder).fetchOne();

		return new PageImpl<>(contents, pageable, total);
	}
}
