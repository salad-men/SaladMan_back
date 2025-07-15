package com.kosta.saladMan.repository.order;

import java.time.LocalDate;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.dto.dashboard.InventoryExpireSummaryDto;
import com.kosta.saladMan.dto.purchaseOrder.LowStockItemDto;
import com.kosta.saladMan.dto.purchaseOrder.StoreOrderItemDto;
import com.kosta.saladMan.entity.inventory.QHqIngredient;
import com.kosta.saladMan.entity.inventory.QIngredient;
import com.kosta.saladMan.entity.inventory.QIngredientCategory;
import com.kosta.saladMan.entity.inventory.QStoreIngredient;
import com.kosta.saladMan.entity.inventory.QStoreIngredientSetting;
import com.kosta.saladMan.entity.purchaseOrder.QFixedOrderItem;
import com.kosta.saladMan.entity.purchaseOrder.QFixedOrderTemplate;
import com.kosta.saladMan.entity.purchaseOrder.QPurchaseOrder;
import com.kosta.saladMan.entity.purchaseOrder.QPurchaseOrderItem;
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
		
		Expression<Integer> totalQuantityExpr = JPAExpressions
			    .select(qstoreIngre.quantity.sum())
			    .from(qstoreIngre)
			    .where(
			        qstoreIngre.ingredient.id.eq(qsetting.ingredient.id),
			        qstoreIngre.store.id.eq(qsetting.store.id),
			        qstoreIngre.expiredDate.gt(LocalDate.now())
			    );

		return jpaQueryFactory
				.select(Projections.constructor(
					    LowStockItemDto.class,
					    qsetting.ingredient.name,
					    qsetting.ingredient.category.name,
					    Expressions.numberTemplate(Integer.class, "coalesce({0}, 0)", totalQuantityExpr),
					    qin.unit,
					    qsetting.minQuantity
					))
					.from(qsetting)
					.leftJoin(qin).on(qsetting.ingredient.id.eq(qin.id))
					.where(
					    qsetting.store.id.eq(id),
					    qsetting.minQuantity.gt(
					        Expressions.numberTemplate(Integer.class, "coalesce({0}, 0)", totalQuantityExpr)
					        .add(
					            JPAExpressions.select(
					                qpi.orderedQuantity.subtract(qpi.receivedQuantity).sum().coalesce(0)
					            )
					            .from(qpi)
					            .join(qpi.purchaseOrder, qpo)
					            .where(
					                qpo.store.id.eq(id),
					                qpi.ingredient.id.eq(qsetting.ingredient.id),
					                qpo.status.in("대기중")
					            )
					        )
					    )
					).fetch();

	}
	
	//발주 신청을 위한 재료 리스트 
	public List<StoreOrderItemDto> findAvailableOrderItemsByStore(Integer id,String category,String keyword){

	    QIngredient ing = QIngredient.ingredient;
	    QIngredientCategory cat = QIngredientCategory.ingredientCategory;
	    QStoreIngredient si = QStoreIngredient.storeIngredient;
	    QHqIngredient hq = QHqIngredient.hqIngredient;
	    QPurchaseOrder po = QPurchaseOrder.purchaseOrder;
	    QPurchaseOrderItem poi = QPurchaseOrderItem.purchaseOrderItem;

	    LocalDate today = LocalDate.now();
	    
	    Expression<Integer> hqStockExpr = ExpressionUtils.as(
	    	    JPAExpressions
	    	        .select(hq.quantity.sum().coalesce(0))
	    	        .from(hq)
	    	        .where(
	    	            hq.ingredient.id.eq(ing.id),
	    	            hq.expiredDate.goe(today),
	    	            hq.quantity.gt(0)
	    	        ),
	    	    "hqStock"
	    	);

	    Expression<Integer> incomingExpr = ExpressionUtils.as(
	    	    JPAExpressions
	    	        .select(poi.orderedQuantity.sum().coalesce(0))
	    	        .from(poi)
	    	        .join(poi.purchaseOrder, po)
	    	        .where(
	    	            poi.ingredient.id.eq(ing.id),
	    	            po.store.id.eq(id),
	    	            po.status.in("대기중", "입고완료")
	    	        ),
	    	    "incoming"
	    	);
	    
	    Expression<Integer> storeQuantityExpr = ExpressionUtils.as(
	    	    JPAExpressions
	    	        .select(si.quantity.sum().coalesce(0))
	    	        .from(si)
	    	        .where(
	    	            si.ingredient.id.eq(ing.id),
	    	            si.store.id.eq(id),
	    	            si.expiredDate.goe(today)
	    	        ),
	    	    "storeQuantity"
	    	);
	    
	    return jpaQueryFactory
	    	    .select(Projections.constructor(
	    	        StoreOrderItemDto.class,
	    	        ing.id,
	    	        ing.name,
	    	        cat.id,
	    	        cat.name,
	    	        storeQuantityExpr,
	    	        incomingExpr,
	    	        ing.unit,
	    	        hq.unitCost.max().coalesce(0),
	    	        hq.minimumOrderUnit.min().coalesce(0),
	    	        hqStockExpr,
	    	        ing.available
	    	    ))
	    	    .from(ing)
	    	    .join(ing.category, cat)
	    	    .leftJoin(hq).on(hq.ingredient.id.eq(ing.id))
	    	    .where(
	    	       	    category != null && !category.equals("전체")
	    	    	        ? cat.name.eq(category) : null,

	    	    	    keyword != null && !keyword.isBlank()
	    	    	        ? ing.name.containsIgnoreCase(keyword) : null
	    	    )
	    	    .groupBy(
	    	        ing.id, ing.name, cat.id, cat.name,
	    	        ing.unit, ing.available
	    	    )
	    	    .fetch();
	}
	
	
	
	 // 임박재고 Top3+전체건수+임박카운트
    public InventoryExpireSummaryDto findExpireSummaryTop3WithCountMerged(String startDate, String endDate) {
        QStoreIngredient si = QStoreIngredient.storeIngredient;

        LocalDate sDate = (startDate != null && !startDate.isBlank()) ? LocalDate.parse(startDate) : null;
        LocalDate eDate = (endDate != null && !endDate.isBlank()) ? LocalDate.parse(endDate) : null;

        // Top3 임박재고
        List<InventoryExpireSummaryDto.Item> top3 = jpaQueryFactory
            .select(Projections.bean(
                InventoryExpireSummaryDto.Item.class,
                si.ingredient.name.as("ingredientName"),
                si.category.name.as("categoryName"),
                si.quantity.as("remainQuantity"),
                si.expiredDate.stringValue().as("expiredDate")
            ))
            .from(si)
            .where(
                si.quantity.gt(0),
                sDate != null ? si.expiredDate.goe(sDate) : null,
                eDate != null ? si.expiredDate.loe(eDate) : null
            )
            .orderBy(si.expiredDate.asc(), si.quantity.desc())
            .limit(3)
            .fetch();

        // 전체 임박재고 건수
        Long totalCount = jpaQueryFactory
            .select(si.count())
            .from(si)
            .where(
                si.quantity.gt(0),
                sDate != null ? si.expiredDate.goe(sDate) : null,
                eDate != null ? si.expiredDate.loe(eDate) : null
            )
            .fetchOne();

        LocalDate today = LocalDate.now();

        // D-1 (내일 유통기한)
        Long d1Count = jpaQueryFactory
            .select(si.count())
            .from(si)
            .where(
                si.quantity.gt(0),
                si.expiredDate.eq(today.plusDays(1))
            )
            .fetchOne();

        // D-DAY (오늘 유통기한)
        Long todayCount = jpaQueryFactory
            .select(si.count())
            .from(si)
            .where(
                si.quantity.gt(0),
                si.expiredDate.eq(today)
            )
            .fetchOne();

        InventoryExpireSummaryDto dto = new InventoryExpireSummaryDto();
        dto.setTop3(top3);
        dto.setTotalCount(totalCount != null ? totalCount.intValue() : 0);
        dto.setD1Count(d1Count != null ? d1Count.intValue() : 0);
        dto.setTodayCount(todayCount != null ? todayCount.intValue() : 0);

        return dto;
    }
    
    
    
    // 자동 발주 예정 품목 수 카운트
    public int countAutoOrderExpectedByStore(Integer storeId) {
        QFixedOrderTemplate template = QFixedOrderTemplate.fixedOrderTemplate;
        QFixedOrderItem item = QFixedOrderItem.fixedOrderItem;

        Long count = jpaQueryFactory
                .select(item.count())
                .from(item)
                .join(item.fixedOrderTemplate, template)
                .where(
                    template.store.id.eq(storeId),
                    item.autoOrderEnabled.isTrue()
                )
                .fetchOne();

        return count != null ? count.intValue() : 0;
    }


}
