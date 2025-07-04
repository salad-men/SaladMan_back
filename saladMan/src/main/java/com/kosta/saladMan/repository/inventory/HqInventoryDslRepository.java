package com.kosta.saladMan.repository.inventory;

import com.kosta.saladMan.dto.inventory.HqIngredientDto;
import com.kosta.saladMan.dto.inventory.InventoryRecordDto;
import com.kosta.saladMan.dto.inventory.StoreIngredientSettingDto;
import com.kosta.saladMan.entity.inventory.Disposal;
import com.kosta.saladMan.entity.inventory.Ingredient;
import com.kosta.saladMan.entity.inventory.QDisposal;
import com.kosta.saladMan.entity.inventory.QHqIngredient;
import com.kosta.saladMan.entity.inventory.QIngredient;
import com.kosta.saladMan.entity.inventory.QIngredientCategory;
import com.kosta.saladMan.entity.inventory.QInventoryRecord;
import com.kosta.saladMan.entity.inventory.QStoreIngredientSetting;
import com.kosta.saladMan.entity.store.QStore;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public class HqInventoryDslRepository {

	@Autowired
	private JPAQueryFactory queryFactory;

	// 재고 조회
	public List<HqIngredientDto> findHqInventoryByFilters(
	        Integer categoryId, String keyword, LocalDate startDate, LocalDate endDate, PageRequest pageRequest) {

	    QHqIngredient q = QHqIngredient.hqIngredient;
	    QStoreIngredientSetting s = QStoreIngredientSetting.storeIngredientSetting;
	    QStore store = QStore.store;

	    BooleanBuilder builder = new BooleanBuilder();

	    if (categoryId != null) {
	        builder.and(q.category.id.eq(categoryId));
	    }
	    if (keyword != null && !keyword.isBlank()) {
	        builder.and(q.ingredient.name.containsIgnoreCase(keyword));
	    }
	    if (startDate != null) builder.and(q.expiredDate.goe(startDate));
	    if (endDate != null) builder.and(q.expiredDate.loe(endDate));

	    builder.and(q.quantity.gt(0));

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
	            s.minQuantity.as("minquantity")
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
	        .orderBy(q.id.desc())
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
	@Transactional
	public void updateHqIngredient(HqIngredientDto dto) {
		QHqIngredient q = QHqIngredient.hqIngredient;
		JPAUpdateClause clause = 
				queryFactory
				.update(q)
				.set(q.minimumOrderUnit, dto.getMinimumOrderUnit())
				.set(q.unitCost, dto.getUnitCost())
				.set(q.quantity, dto.getQuantity())
				.set(q.expiredDate, dto.getExpiredDate())
				.where(q.id.eq(dto.getId()));
		clause.execute();
	}

	//폐기 개수 조회
    public int countHqDisposals(Integer storeId, Integer categoryId, String keyword, LocalDate startDate, LocalDate endDate) {
        QDisposal disposal = QDisposal.disposal;
        BooleanBuilder builder = new BooleanBuilder();

        if (storeId != null) {
            builder.and(disposal.store.id.eq(storeId));
        }
        if (categoryId != null) {
            builder.and(disposal.ingredient.category.id.eq(categoryId));
        }
        if (keyword != null && !keyword.isEmpty()) {
            builder.and(disposal.ingredient.name.containsIgnoreCase(keyword));
        }
        if (startDate != null) {
            builder.and(disposal.requestedAt.goe(startDate));
        }
        if (endDate != null) {
            builder.and(disposal.requestedAt.loe(endDate));
        }

        Long count = queryFactory
                .select(disposal.count())
                .from(disposal)
                .where(builder)
                .fetchOne();

        return count != null ? count.intValue() : 0;
    }
    
    //폐기조회
    public List<Disposal> selectHqDisposalListByFiltersPaging(
            Integer storeId, Integer categoryId, String keyword, LocalDate startDate, LocalDate endDate, PageRequest pageRequest) {

        QDisposal disposal = QDisposal.disposal;
        BooleanBuilder builder = new BooleanBuilder();

        if (storeId != null) {
            builder.and(disposal.store.id.eq(storeId));
        }
        if (categoryId != null) {
            builder.and(disposal.ingredient.category.id.eq(categoryId));
        }
        if (keyword != null && !keyword.isEmpty()) {
            builder.and(disposal.ingredient.name.containsIgnoreCase(keyword));
        }
        if (startDate != null) {
            builder.and(disposal.requestedAt.goe(startDate));
        }
        if (endDate != null) {
            builder.and(disposal.requestedAt.loe(endDate));
        }

        return queryFactory.selectFrom(disposal)
                .leftJoin(disposal.store)
                .leftJoin(disposal.ingredient)
                .leftJoin(disposal.ingredient.category)
                .where(builder)
                .orderBy(disposal.requestedAt.desc())
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
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
}