package com.kosta.saladMan.repository.inventory;

import com.kosta.saladMan.dto.dashboard.DisposalSummaryDto;
import com.kosta.saladMan.dto.dashboard.InventoryExpireSummaryDto;
import com.kosta.saladMan.dto.inventory.HqIngredientDto;
import com.kosta.saladMan.dto.inventory.InventoryRecordDto;
import com.kosta.saladMan.dto.inventory.StoreIngredientSettingDto;
import com.kosta.saladMan.entity.inventory.Disposal;
import com.kosta.saladMan.entity.inventory.Ingredient;
import com.kosta.saladMan.entity.inventory.IngredientCategory;
import com.kosta.saladMan.entity.inventory.QDisposal;
import com.kosta.saladMan.entity.inventory.QHqIngredient;
import com.kosta.saladMan.entity.inventory.QIngredient;
import com.kosta.saladMan.entity.inventory.QIngredientCategory;
import com.kosta.saladMan.entity.inventory.QInventoryRecord;
import com.kosta.saladMan.entity.inventory.QStoreIngredientSetting;
import com.kosta.saladMan.entity.store.QStore;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class HqInventoryDslRepository {

	@Autowired
	private JPAQueryFactory queryFactory;

	// 재고 조회
	// HqInventoryDslRepository.java

	public List<HqIngredientDto> findHqInventoryByFilters(
	        Integer categoryId, String keyword, LocalDate startDate, LocalDate endDate,
	        PageRequest pageRequest, String sortOption) {

	    QHqIngredient q = QHqIngredient.hqIngredient;
	    QStoreIngredientSetting s = QStoreIngredientSetting.storeIngredientSetting;
	    QStore store = QStore.store;

	    BooleanBuilder builder = new BooleanBuilder();

	    if (categoryId != null) builder.and(q.category.id.eq(categoryId));
	    if (keyword != null && !keyword.isBlank()) builder.and(q.ingredient.name.containsIgnoreCase(keyword));
	    if (startDate != null) builder.and(q.expiredDate.goe(startDate));
	    if (endDate != null) builder.and(q.expiredDate.loe(endDate));
	    builder.and(q.quantity.gt(0));

	    List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
	    if ("receivedAsc".equals(sortOption)) {
	        orderSpecifiers.add(q.receivedDate.asc().nullsLast());
	        orderSpecifiers.add(q.category.name.asc());
	        orderSpecifiers.add(q.ingredient.name.asc());
	    } else if ("receivedDesc".equals(sortOption)) {
	        orderSpecifiers.add(q.receivedDate.desc().nullsLast());
	        orderSpecifiers.add(q.category.name.asc());
	        orderSpecifiers.add(q.ingredient.name.asc());
	    } else if ("expiryAsc".equals(sortOption)) {
	        orderSpecifiers.add(q.expiredDate.asc().nullsLast());
	        orderSpecifiers.add(q.category.name.asc());
	        orderSpecifiers.add(q.ingredient.name.asc());
	    } else if ("expiryDesc".equals(sortOption)) {
	        orderSpecifiers.add(q.expiredDate.desc().nullsLast());
	        orderSpecifiers.add(q.category.name.asc());
	        orderSpecifiers.add(q.ingredient.name.asc());
	    } else {
	        // 기본 분류-재료-유통기한 오름차순
	        orderSpecifiers.add(q.category.name.asc());
	        orderSpecifiers.add(q.ingredient.name.asc());
	        orderSpecifiers.add(q.expiredDate.asc().nullsLast());
	    }

	    return queryFactory
	        .select(Projections.bean(
	            HqIngredientDto.class,
	            q.id,
	            q.quantity,
	            q.minimumOrderUnit,
	            q.unitCost,
	            q.expiredDate,
	            q.receivedDate,
	            q.ingredient.name.as("ingredientName"),
	            q.ingredient.unit.as("unit"),
	            q.category.name.as("categoryName"),
	            store.name.as("storeName"),
	            s.minQuantity.as("minquantity"),
	            q.category.id.as("categoryId"),
	            q.ingredient.id.as("ingredientId")
	        ))
	        .from(q)
	        .leftJoin(q.ingredient)
	        .leftJoin(q.category)
	        .leftJoin(q.store, store)
	        .leftJoin(s).on(
	            s.store.id.eq(store.id)
	            .and(s.ingredient.eq(q.ingredient))
	        )
	        .where(builder)
	        .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0])) // ← 이거!
	        .offset(pageRequest.getOffset())
	        .limit(pageRequest.getPageSize())
	        .fetch();
	}


	// 재고 개수 조회
	public long countHqInventoryByFilters(Integer categoryId, String keyword, LocalDate startDate, LocalDate endDate) {
	    QHqIngredient q = QHqIngredient.hqIngredient;
	    BooleanBuilder builder = new BooleanBuilder();

	    if (categoryId != null) {
	        builder.and(q.category.id.eq(categoryId));
	    }
	    if (keyword != null && !keyword.isBlank()) {
	        builder.and(q.ingredient.name.containsIgnoreCase(keyword));
	    }
	    if (startDate != null) builder.and(q.expiredDate.goe(startDate));
	    if (endDate != null) builder.and(q.expiredDate.loe(endDate));

	    Long count = queryFactory.select(q.count()).from(q).where(builder).fetchOne();
	    return count == null ? 0L : count;
	}




	// 재고 업데이트
	// HqInventoryDslRepository.java

	@Transactional
	public void updateHqIngredient(HqIngredientDto dto) {
	    QHqIngredient q = QHqIngredient.hqIngredient;

	    // category, ingredient, store 객체 생성
	    IngredientCategory category = null;
	    if (dto.getCategoryId() != null)
	        category = IngredientCategory.builder().id(dto.getCategoryId()).build();

	    Ingredient ingredient = null;
	    if (dto.getIngredientId() != null)
	        ingredient = Ingredient.builder().id(dto.getIngredientId()).build();

	    
	    JPAUpdateClause clause = queryFactory
	        .update(q)
	        .set(q.category, category)
	        .set(q.ingredient, ingredient)
	        .set(q.minimumOrderUnit, dto.getMinimumOrderUnit())
	        .set(q.unitCost, dto.getUnitCost())
	        .set(q.quantity, dto.getQuantity())
	        .set(q.expiredDate, dto.getExpiredDate())
	        .set(q.receivedDate, dto.getReceivedDate())
	        .set(q.reservedQuantity, dto.getReservedQuantity()) 
	        
	        .where(q.id.eq(dto.getId()));
	    clause.execute();
	}


	// 폐기 개수 조회
    public int countHqDisposals(Integer storeId, Integer categoryId, String status,
                                LocalDate startDate, LocalDate endDate, String keyword) {
        QDisposal d = QDisposal.disposal;
        BooleanBuilder builder = new BooleanBuilder();

        if (storeId != null) builder.and(d.store.id.eq(storeId));
        if (categoryId != null) builder.and(d.ingredient.category.id.eq(categoryId));
        if (status != null && !"all".equals(status)) builder.and(d.status.eq(status));
        if (startDate != null) builder.and(d.requestedAt.goe(startDate));
        if (endDate != null) builder.and(d.requestedAt.loe(endDate));
        if (keyword != null && !keyword.isBlank()) {
            builder.and(d.ingredient.name.containsIgnoreCase(keyword));
        }

        Long cnt = queryFactory.select(d.count()).from(d).where(builder).fetchOne();
        return cnt == null ? 0 : cnt.intValue();
    }

    // 폐기목록 조회 
    public List<Disposal> selectHqDisposalListByFiltersPaging(
            Integer storeId, Integer categoryId, String status,
            LocalDate startDate, LocalDate endDate,
            int offset, int limit,
            String sortOption, String keyword) {
        
        QDisposal d = QDisposal.disposal;
        BooleanBuilder builder = new BooleanBuilder();

        if (storeId != null) builder.and(d.store.id.eq(storeId));
        if (categoryId != null) builder.and(d.ingredient.category.id.eq(categoryId));
        if (status != null && !"all".equals(status)) builder.and(d.status.eq(status));
        if (startDate != null) builder.and(d.requestedAt.goe(startDate));
        if (endDate != null) builder.and(d.requestedAt.loe(endDate));
        if (keyword != null && !keyword.isBlank()) {
            builder.and(d.ingredient.name.containsIgnoreCase(keyword));
        }

        List<OrderSpecifier<?>> orders = new ArrayList<>();
        switch (sortOption) {
            case "dateAsc":
                orders.add(d.requestedAt.asc());
                break;
            case "dateDesc":
                orders.add(d.requestedAt.desc());
                break;
            default:
                orders.add(d.ingredient.category.name.asc());
                orders.add(d.ingredient.name.asc());
                orders.add(d.requestedAt.desc());
        }

        return queryFactory
                .selectFrom(d)
                .where(builder)
                .orderBy(orders.toArray(new OrderSpecifier[0]))
                .offset(offset)
                .limit(limit)
                .fetch();
    }


    // 상태 변경 (승인/반려)
    public void updateDisposalStatus(List<Integer> disposalIds, String status, String memo) {
        QDisposal disposal = QDisposal.disposal;
        queryFactory.update(disposal)
                .set(disposal.status, status)
                .set(disposal.memo, memo)
                .where(disposal.id.in(disposalIds))
                .execute();
    }
    
    //전체 재료조회
    public List<Ingredient> getAllIngredients() {
        QIngredient ingredient = QIngredient.ingredient;
        QIngredientCategory category = QIngredientCategory.ingredientCategory;

        return queryFactory
            .selectFrom(ingredient)
            .leftJoin(ingredient.category, category)
            .fetch();
    }
    
    //재고 기록 조회
    public List<InventoryRecordDto> findHqInventoryRecords(String changeType, PageRequest pageRequest) {
        QInventoryRecord ir = QInventoryRecord.inventoryRecord;
        QIngredient ingredient = QIngredient.ingredient;
        QIngredientCategory category = QIngredientCategory.ingredientCategory;
        QStore store = QStore.store;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(ir.store.id.eq(1)); // 본사 storeId=1
        if (changeType != null && !changeType.isBlank()) {
            builder.and(ir.changeType.eq(changeType));
        }

        return queryFactory
            .select(Projections.bean(
                InventoryRecordDto.class,
                ir.id,
                ir.quantity,
                ir.changeType,
                ir.memo,
                ir.date,
                ingredient.id.as("ingredientId"),
                ingredient.name.as("ingredientName"),
                category.name.as("categoryName"),
                store.id.as("storeId"),
                store.name.as("storeName")
            ))
            .from(ir)
            .leftJoin(ir.ingredient, ingredient)
            .leftJoin(ingredient.category, category)
            .leftJoin(ir.store, store)
            .where(builder)
            .orderBy(ir.date.desc())
            .offset(pageRequest.getOffset())
            .limit(pageRequest.getPageSize())
            .fetch();
    }
    
 // 재고 설정 개수 조회 (본사)
    public long countHqSettingsByFilters(Integer storeId, Integer categoryId, String keyword) {
        QStoreIngredientSetting setting = QStoreIngredientSetting.storeIngredientSetting;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(setting.store.id.eq(storeId));
        if (categoryId != null) builder.and(setting.ingredient.category.id.eq(categoryId));
        if (keyword != null && !keyword.isEmpty())
            builder.and(setting.ingredient.name.containsIgnoreCase(keyword));

        Long count = queryFactory.select(setting.count()).from(setting).where(builder).fetchOne();
        return count == null ? 0 : count;
    }

    // 재고 설정 목록 조회 (본사) - 페이징 포함
    public List<StoreIngredientSettingDto> findHqSettingsByFilters(
            Integer storeId, Integer categoryId, String keyword, int offset, int limit) {
        QStoreIngredientSetting setting = QStoreIngredientSetting.storeIngredientSetting;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(setting.store.id.eq(storeId));
        if (categoryId != null) builder.and(setting.ingredient.category.id.eq(categoryId));
        if (keyword != null && !keyword.isEmpty())
            builder.and(setting.ingredient.name.containsIgnoreCase(keyword));

        return queryFactory
                .select(Projections.bean(StoreIngredientSettingDto.class,
                        setting.id,
                        setting.minQuantity,
                        setting.maxQuantity,
                        setting.ingredient.id.as("ingredientId"),
                        setting.ingredient.category.name.as("categoryName")
                ))
                .from(setting)
                .where(builder)
                .offset(offset)
                .limit(limit)
                .fetch();
    }
    
    
    
 // 임박재고 Top3+전체건수+임박카운트
    public InventoryExpireSummaryDto findExpireSummaryTop3WithCountMerged(String startDate, String endDate) {
        QHqIngredient hq = QHqIngredient.hqIngredient;

        LocalDate sDate = (startDate != null && !startDate.isBlank()) ? LocalDate.parse(startDate) : null;
        LocalDate eDate = (endDate != null && !endDate.isBlank()) ? LocalDate.parse(endDate) : null;

        // Top3 조회
        List<InventoryExpireSummaryDto.Item> top3 = queryFactory
            .select(Projections.bean(
                InventoryExpireSummaryDto.Item.class,
                hq.ingredient.name.as("ingredientName"),
                hq.category.name.as("categoryName"),
                hq.quantity.as("remainQuantity"),
                hq.expiredDate.stringValue().as("expiredDate")
            ))
            .from(hq)
            .where(
                hq.quantity.gt(0),
                sDate != null ? hq.expiredDate.goe(sDate) : null,
                eDate != null ? hq.expiredDate.loe(eDate) : null
            )
            .orderBy(hq.expiredDate.asc(), hq.quantity.desc())
            .limit(3)
            .fetch();

        // 전체 임박재고 건수
        Long totalCount = queryFactory
            .select(hq.count())
            .from(hq)
            .where(
                hq.quantity.gt(0),
                sDate != null ? hq.expiredDate.goe(sDate) : null,
                eDate != null ? hq.expiredDate.loe(eDate) : null
            )
            .fetchOne();

        // 오늘 기준 계산 (D-1, D-DAY)
        LocalDate today = LocalDate.now();

        // D-1 (내일이 유통기한인 건)
        Long d1Count = queryFactory
            .select(hq.count())
            .from(hq)
            .where(
                hq.quantity.gt(0),
                hq.expiredDate.eq(today.plusDays(1))
            )
            .fetchOne();

        // D-DAY (오늘이 유통기한인 건)
        Long todayCount = queryFactory
            .select(hq.count())
            .from(hq)
            .where(
                hq.quantity.gt(0),
                hq.expiredDate.eq(today)
            )
            .fetchOne();

        InventoryExpireSummaryDto dto = new InventoryExpireSummaryDto();
        dto.setTop3(top3);
        dto.setTotalCount(totalCount != null ? totalCount.intValue() : 0);
        dto.setD1Count(d1Count != null ? d1Count.intValue() : 0);
        dto.setTodayCount(todayCount != null ? todayCount.intValue() : 0);

        return dto;
    }


    // 폐기 Top3+전체건수 (단일 DTO)
    public DisposalSummaryDto findDisposalSummaryTop3WithCountMerged(String startDate, String endDate) {
        QDisposal d = QDisposal.disposal;

        LocalDate sDate = (startDate != null && !startDate.isBlank()) ? LocalDate.parse(startDate) : null;
        LocalDate eDate = (endDate != null && !endDate.isBlank()) ? LocalDate.parse(endDate) : null;

        List<DisposalSummaryDto.Item> top3 = queryFactory
            .select(Projections.bean(
                DisposalSummaryDto.Item.class,
                d.ingredient.name.as("ingredientName"),
                d.ingredient.category.name.as("categoryName"),
                d.quantity,
                d.requestedAt.stringValue().as("requestedAt")
            ))
            .from(d)
            .where(
                sDate != null ? d.requestedAt.goe(sDate) : null,
                eDate != null ? d.requestedAt.loe(eDate) : null
            )
            .orderBy(d.requestedAt.desc())
            .limit(3)
            .fetch();

        Long totalCount = queryFactory
            .select(d.count())
            .from(d)
            .where(
                sDate != null ? d.requestedAt.goe(sDate) : null,
                eDate != null ? d.requestedAt.loe(eDate) : null
            )
            .fetchOne();

        DisposalSummaryDto dto = new DisposalSummaryDto();
        dto.setTop3(top3);
        dto.setTotalCount(totalCount != null ? totalCount.intValue() : 0);

        return dto;
    }

}