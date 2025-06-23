package com.kosta.saladMan.repository.menu;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.dto.inventory.IngredientDto;
import com.kosta.saladMan.dto.menu.MenuIngredientDto;
import com.kosta.saladMan.dto.menu.RecipeDto;
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

    public List<RecipeDto> findAllMenusWithIngredients() {
        List<Tuple> rows = queryFactory
            .select(
            	tm.id,
                tm.name,
                tm.img,
                mi.menu,
                mi.quantity,
                ing.name
            )
            .from(tm)
            .join(mi).on(mi.menu.id.eq(tm.id))
            .join(ing).on(ing.id.eq(mi.ingredient.id))
            .fetch();

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
                IngredientDto.builder()
                    .quantity(row.get(mi.quantity))
                    .name(row.get(ing.name))
                    .build()
            );
        }

        return new ArrayList<>(resultMap.values());
    }
}
