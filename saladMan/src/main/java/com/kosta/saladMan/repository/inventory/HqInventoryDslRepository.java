package com.kosta.saladMan.repository.inventory;

import com.kosta.saladMan.dto.inventory.HqIngredientDto;
import com.kosta.saladMan.entity.inventory.Disposal;
import com.kosta.saladMan.entity.inventory.HqIngredient;
import com.kosta.saladMan.entity.inventory.Ingredient;
import com.kosta.saladMan.entity.inventory.QDisposal;
import com.kosta.saladMan.entity.inventory.QHqIngredient;
import com.kosta.saladMan.entity.inventory.QIngredient;
import com.kosta.saladMan.entity.inventory.QIngredientCategory;
import com.kosta.saladMan.entity.inventory.QStoreIngredient;
import com.kosta.saladMan.entity.inventory.QStoreIngredientSetting;
import com.kosta.saladMan.entity.inventory.StoreIngredient;
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
import org.springframework.data.domain.PageRequest;

@Repository
public class HqInventoryDslRepository {

	@Autowired
	private JPAQueryFactory queryFactory;

	// HQ 재고 목록 조회
	public List<HqIngredientDto> selectHqListByPaging(PageRequest pageRequest, String category, String name) {
		QHqIngredient q = QHqIngredient.hqIngredient;
	    QStoreIngredientSetting s = QStoreIngredientSetting.storeIngredientSetting;

		BooleanBuilder builder = new BooleanBuilder();
		if (category != null && !category.equals("all")) {
			builder.and(q.category.name.eq(category));
		}
		
		if (name != null && !name.isBlank()) {
			builder.and(q.ingredient.name.contains(name));
		}
		
		 return queryFactory
		            .select(Projections.bean(
		                HqIngredientDto.class,
		                q.id,
		                q.quantity,
		                q.minimumOrderUnit,
		                q.unitCost,
		                q.expiredQuantity,
		                q.expiredDate,
		                q.ingredient.name.as("ingredientName"),
		                q.category.name.as("categoryName"),
		                s.minQuantity.as("minQuantity")  
		            ))
		            .from(q)
		            .leftJoin(q.ingredient).fetchJoin()
		            .leftJoin(q.category).fetchJoin()
		            .leftJoin(s)
		            .on(
		                s.store.name.eq("본사계정")
		                .and(s.ingredient.eq(q.ingredient))
		            )
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
		Long count = queryFactory.select(q.count()).from(q).where(builder).fetchOne();
		return count != null ? count : 0L;
	}


	// HQ 재고 업데이트
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

	//본사 유통기한 개수
	public long selectHqCountByExpirationFilters(String category, String keyword, LocalDate startDate, LocalDate endDate) {
	    QHqIngredient q = QHqIngredient.hqIngredient;
	    BooleanBuilder builder = new BooleanBuilder();
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
        System.out.println("selectHqCountByExpirationFilters count=" + (count == null ? 0L : count));
	    return count == null ? 0L : count;
	}

	//매장 유통기한 목록
	public List<HqIngredient> selectHqListByExpirationFiltersPaging(
	        String category, String keyword, LocalDate startDate, LocalDate endDate, PageRequest pageRequest) {
	    QHqIngredient q = QHqIngredient.hqIngredient;
	    BooleanBuilder builder = new BooleanBuilder();
	    if (category != null && !category.isBlank() && !"all".equalsIgnoreCase(category)) {
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
	    List<HqIngredient> result = queryFactory.selectFrom(q)
                .leftJoin(q.ingredient).fetchJoin()
                .leftJoin(q.category).fetchJoin()
                .where(builder)
                .orderBy(q.expiredDate.asc())
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

        System.out.println("selectHqListByExpirationFiltersPaging result size=" + result.size());
        return result;
	}
	
	 // 폐기 총 개수 (필터)
    public int countHqDisposals(String store, String category, String keyword, LocalDate startDate, LocalDate endDate) {
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

    // 본사 폐기 목록 조회
    public List<Disposal> selectHqDisposalListByFiltersPaging(String store, String category, String keyword, LocalDate startDate, LocalDate endDate, PageRequest pageRequest) {
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

    // 상태 변경 (승인/반려)
    public void updateDisposalStatus(List<Integer> disposalIds, String status, String memo) {
        QDisposal disposal = QDisposal.disposal;
        queryFactory.update(disposal)
                .set(disposal.status, status)
                .set(disposal.memo, memo)
                .where(disposal.id.in(disposalIds))
                .execute();
    }
    
    public List<Ingredient> getAllIngredients() {
        QIngredient ingredient = QIngredient.ingredient;
        QIngredientCategory category = QIngredientCategory.ingredientCategory;

        return queryFactory
            .selectFrom(ingredient)
            .leftJoin(ingredient.category, category).fetchJoin()
            .fetch();
    }

}