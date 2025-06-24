package com.kosta.saladMan.repository.inventory;

import com.kosta.saladMan.dto.inventory.InventoryRecordDto;
import com.kosta.saladMan.dto.inventory.StoreIngredientDto;
import com.kosta.saladMan.dto.inventory.StoreIngredientSettingDto;
import com.kosta.saladMan.entity.inventory.Disposal;
import com.kosta.saladMan.entity.inventory.QDisposal;
import com.kosta.saladMan.entity.inventory.QHqIngredient;
import com.kosta.saladMan.entity.inventory.QIngredient;
import com.kosta.saladMan.entity.inventory.QIngredientCategory;
import com.kosta.saladMan.entity.inventory.QInventoryRecord;
import com.kosta.saladMan.entity.inventory.QStoreIngredient;
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
public class StoreInventoryDslRepository {

    @Autowired
    private JPAQueryFactory queryFactory;

    // 매장 재고 조회
    public List<StoreIngredientDto> findStoreInventoryByFilters(
            Integer storeId, Integer categoryId, String keyword,
            LocalDate startDate, LocalDate endDate, PageRequest pageRequest) {

        QStoreIngredient q = QStoreIngredient.storeIngredient;
        QStore store = QStore.store;
        QStoreIngredientSetting s = QStoreIngredientSetting.storeIngredientSetting;
        QHqIngredient hq = QHqIngredient.hqIngredient;

        BooleanBuilder builder = new BooleanBuilder();

        if (storeId != null) {
            builder.and(q.store.id.eq(storeId));
        }
        
        if (categoryId != null) {
            builder.and(q.category.id.eq(categoryId));
        }
        if (keyword != null && !keyword.isBlank()) {
            builder.and(q.ingredient.name.containsIgnoreCase(keyword));
        }
        if (startDate != null) builder.and(q.expiredDate.goe(startDate));
        if (endDate != null) builder.and(q.expiredDate.loe(endDate));

        return queryFactory
                .select(Projections.bean(
                        StoreIngredientDto.class,
                        q.id,
                        q.quantity,
                        q.expiredDate,
                        q.receivedDate,
                        q.ingredient.name.as("ingredientName"),
                        q.ingredient.unit.as("unit"),
                        q.category.name.as("categoryName"),
                        store.name.as("storeName"),
                        s.minQuantity.as("minQuantity"),
                        hq.unitCost.as("unitCost"),
                        hq.minimumOrderUnit.as("minimumOrderUnit")
                ))
                .from(q)
                .leftJoin(q.ingredient)
                .leftJoin(q.category)
                .leftJoin(q.store, store)
                .leftJoin(s).on(
                        s.store.id.eq(store.id)
                                .and(s.ingredient.eq(q.ingredient))
                )
                .leftJoin(hq)
                .on(hq.ingredient.eq(q.ingredient)
                        .and(hq.store.id.eq(1)))  // 본사 storeId=1
                .where(builder)
                .orderBy(q.expiredDate.asc())
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();
    }
    
    //매장 재고 개수 조회
    public long countStoreInventoryByFilters(
            Integer storeId, Integer categoryId, String keyword,
            LocalDate startDate, LocalDate endDate) {

        QStoreIngredient q = QStoreIngredient.storeIngredient;
        BooleanBuilder builder = new BooleanBuilder();

        if (storeId != null) {
            builder.and(q.store.id.eq(storeId));
        }
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


    // 매장 재고 업데이트
    @Transactional
    public void updateStoreIngredient(StoreIngredientDto dto) {
        QStoreIngredient q = QStoreIngredient.storeIngredient;
        JPAUpdateClause clause = queryFactory.update(q)
                .set(q.quantity, dto.getQuantity())
                .set(q.expiredDate, dto.getExpiredDate())
                .where(q.id.eq(dto.getId()));
        clause.execute();
    }

    // 매장 폐기 개수
    public int countStoreDisposals(Integer storeId, Integer categoryId, String keyword, LocalDate startDate, LocalDate endDate) {
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
        if (startDate != null) builder.and(disposal.requestedAt.goe(startDate));
        if (endDate != null) builder.and(disposal.requestedAt.loe(endDate));

        Long count = queryFactory
                .select(disposal.count())
                .from(disposal)
                .where(builder)
                .fetchOne();

        return count != null ? count.intValue() : 0;
    }

    // 매장 폐기 목록 조회
    public List<Disposal> selectStoreDisposalListByFiltersPaging(
            Integer storeId, Integer categoryId, String keyword,
            LocalDate startDate, LocalDate endDate,
            PageRequest pageRequest) {

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
        if (startDate != null) builder.and(disposal.requestedAt.goe(startDate));
        if (endDate != null) builder.and(disposal.requestedAt.loe(endDate));

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

    //매장 재고 기록 조회
    public List<InventoryRecordDto> findStoreInventoryRecords(Integer storeId, String changeType, PageRequest pageRequest) {
        QInventoryRecord ir = QInventoryRecord.inventoryRecord;
        QIngredient ingredient = QIngredient.ingredient;
        QIngredientCategory category = QIngredientCategory.ingredientCategory;
        QStore store = QStore.store;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(ir.store.id.eq(storeId));
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

    //매장 설정 개수 조회
    public long countStoreSettingsByFilters(Integer storeId, Integer categoryId, String keyword) {
        QStoreIngredientSetting setting = QStoreIngredientSetting.storeIngredientSetting;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(setting.store.id.eq(storeId));
        if (categoryId != null) builder.and(setting.ingredient.category.id.eq(categoryId));
        if (keyword != null && !keyword.isEmpty())
            builder.and(setting.ingredient.name.containsIgnoreCase(keyword));

        Long count = queryFactory.select(setting.count()).from(setting).where(builder).fetchOne();
        return count == null ? 0 : count;
    }
    //매장 설정 조회
    public List<StoreIngredientSettingDto> findStoreSettingsByFilters(
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
