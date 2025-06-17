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

	// store 재고 목록 조회
	public List<StoreIngredient> selectStoreListByPaging(PageRequest pageRequest, String store, String category,
			String name) {
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
		return queryFactory.selectFrom(q).leftJoin(q.ingredient).fetchJoin().leftJoin(q.category).fetchJoin()
				.where(builder).orderBy(q.id.desc()).offset(pageRequest.getOffset()).limit(pageRequest.getPageSize())
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
		Long count = queryFactory.select(q.count()).from(q).where(builder).fetchOne();
		return count != null ? count : 0L;
	}
	
	// 매장 재고 업데이트
	@Transactional
	public void updateStoreIngredient(StoreIngredient entity) {
		QStoreIngredient q = QStoreIngredient.storeIngredient;
		JPAUpdateClause clause = queryFactory.update(q).set(q.quantity, entity.getQuantity())
				.where(q.id.eq(entity.getId()));
		clause.execute();
	}


	//매장 유통기한 목록 개수
	public long selectStoreCountByExpirationFilters(String store, String category, String keyword, LocalDate startDate, LocalDate endDate) {
	    QStoreIngredient q = QStoreIngredient.storeIngredient;
	    BooleanBuilder builder = new BooleanBuilder();
	    if (store != null && !store.isBlank()) {
	        builder.and(q.store.name.eq(store));
	    }
	    if (category != null && !"all".equalsIgnoreCase(category)) {
	        builder.and(q.category.name.eq(category));
	    }
	    if (keyword != null && !keyword.isBlank()) {
	        builder.and(q.ingredient.name.containsIgnoreCase(keyword));
	    }
	    if (startDate != null) {
	        builder.and(q.expiredDate.goe(startDate));
	    }
	    if (endDate != null) {
	        builder.and(q.expiredDate.loe(endDate));
	    }
	    Long count = queryFactory.select(q.count()).from(q).where(builder).fetchOne();
	    return count == null ? 0L : count;
	}
	
	// 매장 유통기한 목록
	public List<StoreIngredient> selectStoreListByExpirationFiltersPaging(
	        String store, String category, String keyword, LocalDate startDate, LocalDate endDate, PageRequest pageRequest) {
	    QStoreIngredient q = QStoreIngredient.storeIngredient;
	    BooleanBuilder builder = new BooleanBuilder();
	    if (store != null && !store.isBlank()) {
	        builder.and(q.store.name.eq(store));
	    }
	    if (category != null && !"all".equalsIgnoreCase(category)) {
	        builder.and(q.category.name.eq(category));
	    }
	    if (keyword != null && !keyword.isBlank()) {
	        builder.and(q.ingredient.name.containsIgnoreCase(keyword));
	    }
	    if (startDate != null) {
	        builder.and(q.expiredDate.goe(startDate));
	    }
	    if (endDate != null) {
	        builder.and(q.expiredDate.loe(endDate));
	    }
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