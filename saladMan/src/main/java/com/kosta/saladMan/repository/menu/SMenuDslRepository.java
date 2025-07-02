package com.kosta.saladMan.repository.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.dto.inventory.IngredientDto;
import com.kosta.saladMan.dto.menu.MenuIngredientDto;
import com.kosta.saladMan.dto.menu.RecipeDto;
import com.kosta.saladMan.dto.menu.RecipeIngredientDto;
import com.kosta.saladMan.dto.menu.StoreMenuStatusDto;
import com.kosta.saladMan.entity.inventory.QIngredient;
import com.kosta.saladMan.entity.menu.QMenuIngredient;
import com.kosta.saladMan.entity.menu.QStoreMenu;
import com.kosta.saladMan.entity.menu.QTotalMenu;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
@Repository
public class SMenuDslRepository {
	
	QTotalMenu tm = QTotalMenu.totalMenu;
	QStoreMenu sm = QStoreMenu.storeMenu;
	QMenuIngredient mi = QMenuIngredient.menuIngredient;
	QIngredient ing = QIngredient.ingredient;
	
	@Autowired
    private JPAQueryFactory queryFactory;

	public List<StoreMenuStatusDto> findMenuWithStoreStatus(Integer storeId) {

        return queryFactory
            .select(Projections.constructor(
            	StoreMenuStatusDto.class,
                tm.id,
                tm.img,
                tm.name,
                tm.salePrice,
                sm.status
            ))
            .from(tm)
            .leftJoin(sm).on(
                tm.id.eq(sm.menu.id),
                sm.store.id.eq(storeId)
            )
            .fetch();
    }

	public List<RecipeDto> findAllMenusWithIngredients(Pageable pageable) {
	    // 1. 먼저 메뉴 ID만 페이징으로 가져오기
	    List<Integer> menuIds = queryFactory
	        .select(tm.id)
	        .from(tm)
	        .orderBy(tm.id.asc()) // 정렬은 원하는 방식대로
	        .offset(pageable.getOffset())
	        .limit(pageable.getPageSize())
	        .fetch();

	    if (menuIds.isEmpty()) return Collections.emptyList();

	    // 2. 해당 ID들의 재료까지 한번에 조회
	    List<Tuple> rows = queryFactory
	        .select(
	            tm.id,
	            tm.name,
	            tm.img,
	            mi.quantity,
	            ing.name,
	            ing.unit
	        )
	        .from(tm)
	        .join(mi).on(mi.menu.id.eq(tm.id))
	        .join(ing).on(ing.id.eq(mi.ingredient.id))
	        .where(tm.id.in(menuIds))
	        .orderBy(tm.id.asc())
	        .fetch();

	    // 3. 조합
	    Map<Integer, RecipeDto> resultMap = new LinkedHashMap<>();

	    for (Tuple row : rows) {
	        Integer menuId = row.get(tm.id);

	        RecipeDto dto = resultMap.computeIfAbsent(menuId, id -> RecipeDto.builder()
	                .id(id)
	                .name(row.get(tm.name))
	                .img(row.get(tm.img))
	                .ingredients(new ArrayList<>())
	                .build()
	        );

	        dto.getIngredients().add(
	            RecipeIngredientDto.builder()
	                .unit(row.get(ing.unit))
	                .name(row.get(ing.name))
	                .quantity(row.get(mi.quantity))
	                .build()
	        );
	    }

	    return new ArrayList<>(resultMap.values());
	}
}
