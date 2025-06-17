package com.kosta.saladMan.repository.inventory;

import com.kosta.saladMan.dto.inventory.HqIngredientDto;
import com.kosta.saladMan.entity.inventory.HqIngredient;
import com.kosta.saladMan.entity.inventory.QHqIngredient;
import com.kosta.saladMan.entity.inventory.QStoreIngredient;
import com.kosta.saladMan.entity.inventory.StoreIngredient;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public class InventoryDslRepository {

    @Autowired
    private JPAQueryFactory queryFactory;

    // HQ 재고 목록 조회
    public List<HqIngredient> selectHqListByPaging(PageRequest pageRequest, String category, String name) {
        QHqIngredient q = QHqIngredient.hqIngredient;
        BooleanBuilder builder = new BooleanBuilder();
        if (category != null && !category.equals("all")) {
            builder.and(q.category.name.eq(category));
        }
        if (name != null && !name.isBlank()) {
            builder.and(q.ingredient.name.contains(name));
        }
        return queryFactory.selectFrom(q)
                .leftJoin(q.ingredient).fetchJoin()   
                .leftJoin(q.category).fetchJoin()     
                .where(builder)
                .orderBy(q.id.desc())
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();
    }

    // HQ 재고 전체 개수
    public long selectHqCount(String category, String name) {
        QHqIngredient q = QHqIngredient.hqIngredient;
        BooleanBuilder builder = new BooleanBuilder();
        if (category != null && !category.equals("all")) {
            builder.and(q.category.name.eq(category));
        }
        if (name != null && !name.isBlank()) {
            builder.and(q.ingredient.name.contains(name));
        }
        Long count = queryFactory.select(q.count())
                .from(q)
                .where(builder)
                .fetchOne();
        return count != null ? count : 0L;
    }

    // store 재고 목록 조회
    public List<StoreIngredient> selectStoreListByPaging(PageRequest pageRequest, String store, String category, String name) {
        QStoreIngredient q = QStoreIngredient.storeIngredient;
        BooleanBuilder builder = new BooleanBuilder();
        if (store != null && !store.isBlank()) {
            builder.and(q.store.name.eq(store));
        }
        if (category != null && !category.equals("all")) {
            builder.and(q.category.name.eq(category));
        }
        if (name != null && !name.isBlank()) {
            builder.and(q.ingredient.name.contains(name));
        }
        return queryFactory.selectFrom(q)
                .leftJoin(q.ingredient).fetchJoin()  
                .leftJoin(q.category).fetchJoin()     
                .where(builder)
                .orderBy(q.id.desc())
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();
    }

    // store 재고 전체 개수
    public long selectStoreCount(String store, String category, String name) {
        QStoreIngredient q = QStoreIngredient.storeIngredient;
        BooleanBuilder builder = new BooleanBuilder();
        if (store != null && !store.isBlank()) {
            builder.and(q.store.name.eq(store));
        }
        if (category != null && !category.equals("all")) {
            builder.and(q.category.name.eq(category));
        }
        if (name != null && !name.isBlank()) {
            builder.and(q.ingredient.name.contains(name));
        }
        Long count = queryFactory.select(q.count())
                .from(q)
                .where(builder)
                .fetchOne();
        return count != null ? count : 0L;
    }

    // HQ 재고 업데이트
    @Transactional
    public void updateHqIngredient(HqIngredientDto dto) {
        QHqIngredient q = QHqIngredient.hqIngredient;
        JPAUpdateClause clause = queryFactory.update(q)
                .set(q.expiredQuantity, dto.getExpiredQuantity())
                .set(q.minimumOrderUnit, dto.getMinimumOrderUnit())
                .set(q.unitCost, dto.getUnitCost())
                .set(q.quantity, dto.getQuantity())
                .set(q.expiredDate, dto.getExpiredDate())
                .where(q.id.eq(dto.getId()));
        clause.execute();
    }

    // 매장 재고 업데이트
    @Transactional
    public void updateStoreIngredient(StoreIngredient entity) {
        QStoreIngredient q = QStoreIngredient.storeIngredient;
        JPAUpdateClause clause = queryFactory.update(q)
                .set(q.quantity, entity.getQuantity())
                .where(q.id.eq(entity.getId()));
        clause.execute();
    }
}