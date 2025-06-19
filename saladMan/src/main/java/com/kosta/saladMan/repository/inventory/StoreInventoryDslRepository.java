package com.kosta.saladMan.repository.inventory;

import java.time.LocalDate;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.entity.inventory.Disposal;
import com.kosta.saladMan.entity.inventory.QDisposal;
import com.kosta.saladMan.entity.inventory.QIngredient;
import com.kosta.saladMan.entity.inventory.QIngredientCategory;
import com.kosta.saladMan.entity.inventory.QStoreIngredient;
import com.kosta.saladMan.entity.inventory.StoreIngredient;
import com.kosta.saladMan.entity.store.QStore;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;

@Repository

public class StoreInventoryDslRepository {
	@Autowired
	private JPAQueryFactory queryFactory;
	 // 매장 재고(전체/유통기한 등 필터) 목록 조회
    public List<StoreIngredient> findStoreInventoryByFilters(
            Integer storeId, String category, String keyword,
            LocalDate startDate, LocalDate endDate, PageRequest pageRequest
    ) {
        QStoreIngredient q = QStoreIngredient.storeIngredient;
        BooleanBuilder builder = new BooleanBuilder();

        if (storeId != null) {
            builder.and(q.store.id.eq(storeId)); 
        }
        if (category != null && !category.equals("all") && !category.isBlank()) {
            builder.and(q.category.name.eq(category));
        }
        if (keyword != null && !keyword.isBlank()) {
            builder.and(q.ingredient.name.containsIgnoreCase(keyword));
        }
        if (startDate != null) builder.and(q.expiredDate.goe(startDate));
        if (endDate != null) builder.and(q.expiredDate.loe(endDate));

        return queryFactory
                .selectFrom(q)
                .leftJoin(q.ingredient).fetchJoin()
                .leftJoin(q.category).fetchJoin()
                .leftJoin(q.store).fetchJoin()
                .where(builder)
                .orderBy(q.expiredDate.asc())
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();
    }

    // 매장 재고 카운트
    public long countStoreInventoryByFilters(
            Integer storeId, String category, String keyword,
            LocalDate startDate, LocalDate endDate
    ) {
        QStoreIngredient q = QStoreIngredient.storeIngredient;
        BooleanBuilder builder = new BooleanBuilder();

        if (storeId != null) {
            builder.and(q.store.id.eq(storeId)); 
        }
        if (category != null && !category.equals("all") && !category.isBlank()) {
            builder.and(q.category.name.eq(category));
        }
        if (keyword != null && !keyword.isBlank()) {
            builder.and(q.ingredient.name.containsIgnoreCase(keyword));
        }
        if (startDate != null) builder.and(q.expiredDate.goe(startDate));
        if (endDate != null) builder.and(q.expiredDate.loe(endDate));

        Long count = queryFactory.select(q.count()).from(q).where(builder).fetchOne();
        return count == null ? 0L : count;
    }
    
    //매장 전체 조회
    public List<StoreIngredient> findAllStoreInventoryByFilters(
            String category, String keyword,
            LocalDate startDate, LocalDate endDate, PageRequest pageRequest) {

        QStoreIngredient q = QStoreIngredient.storeIngredient;
        BooleanBuilder builder = new BooleanBuilder();

        if (category != null && !category.equals("all") && !category.isBlank()) {
            builder.and(q.category.name.eq(category));
        }
        if (keyword != null && !keyword.isBlank()) {
            builder.and(q.ingredient.name.containsIgnoreCase(keyword));
        }
        if (startDate != null) builder.and(q.expiredDate.goe(startDate));
        if (endDate != null) builder.and(q.expiredDate.loe(endDate));

        return queryFactory.selectFrom(q)
                .leftJoin(q.ingredient).fetchJoin()
                .leftJoin(q.category).fetchJoin()
                .leftJoin(q.store).fetchJoin()
                .where(builder)
                .orderBy(q.expiredDate.asc())
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();
    }

    // 매장 전체 재고 카운트
    public long countAllStoreInventoryByFilters(
            String category, String keyword,
            LocalDate startDate, LocalDate endDate
    ) {
        QStoreIngredient q = QStoreIngredient.storeIngredient;
        BooleanBuilder builder = new BooleanBuilder();

        // storeId 조건 제거 → 전체 지점 대상
        // 카테고리 필터
        if (category != null && !category.equals("all") && !category.isBlank()) {
            builder.and(q.category.name.eq(category));
        }
        // 키워드 필터
        if (keyword != null && !keyword.isBlank()) {
            builder.and(q.ingredient.name.containsIgnoreCase(keyword));
        }
        // 유통기한 시작일 필터
        if (startDate != null) builder.and(q.expiredDate.goe(startDate));
        // 유통기한 종료일 필터
        if (endDate != null) builder.and(q.expiredDate.loe(endDate));

        Long count = queryFactory.select(q.count()).from(q).where(builder).fetchOne();

        return count == null ? 0L : count;
    }

	
	// 매장 재고 업데이트
	@Transactional
	public void updateStoreIngredient(StoreIngredient entity) {
		QStoreIngredient q = QStoreIngredient.storeIngredient;
		JPAUpdateClause clause = queryFactory.update(q).set(q.quantity, entity.getQuantity())
				.where(q.id.eq(entity.getId()));
		clause.execute();
	}


	 // 매장 폐기 총 개수
    public int countStoreDisposals(String store, String category, String keyword, LocalDate startDate, LocalDate endDate) {
        QDisposal disposal = QDisposal.disposal;
        BooleanBuilder builder = new BooleanBuilder();
        if (!"all".equals(store)) builder.and(disposal.store.name.eq(store));
        if (!"all".equals(category)) builder.and(disposal.ingredient.category.name.eq(category));
        if (keyword != null && !keyword.isEmpty()) builder.and(disposal.ingredient.name.containsIgnoreCase(keyword));
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
    public List<Disposal> selectStoreDisposalListByFiltersPaging(String store, String category, String keyword, LocalDate startDate, LocalDate endDate, PageRequest pageRequest) {
        QDisposal disposal = QDisposal.disposal;
        BooleanBuilder builder = new BooleanBuilder();
        if (!"all".equals(store)) builder.and(disposal.store.name.eq(store));
        if (!"all".equals(category)) builder.and(disposal.ingredient.category.name.eq(category));
        if (keyword != null && !keyword.isEmpty()) builder.and(disposal.ingredient.name.containsIgnoreCase(keyword));
        if (startDate != null) builder.and(disposal.requestedAt.goe(startDate));
        if (endDate != null) builder.and(disposal.requestedAt.loe(endDate));
        return queryFactory.selectFrom(disposal)
        	    .leftJoin(disposal.store).fetchJoin()
        	    .leftJoin(disposal.ingredient).fetchJoin()
        	    .leftJoin(disposal.ingredient.category)
        	    .where(builder)
        	    .orderBy(disposal.requestedAt.desc())
        	    .offset(pageRequest.getOffset())
        	    .limit(pageRequest.getPageSize())
        	    .fetch();
    }

}