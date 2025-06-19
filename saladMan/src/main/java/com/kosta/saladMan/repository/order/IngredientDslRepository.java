package com.kosta.saladMan.repository.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.dto.inventory.IngredientItemDto;
import com.kosta.saladMan.entity.inventory.QHqIngredient;
import com.kosta.saladMan.entity.inventory.QIngredient;
import com.kosta.saladMan.entity.inventory.QIngredientCategory;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class IngredientDslRepository {
	@Autowired
	private JPAQueryFactory jpaQueryFactory;

	@Autowired
	private ModelMapper modelMapper;

	public Page<IngredientItemDto> ingredientList(Boolean available, Pageable pageable) {
		QIngredient ingredient = QIngredient.ingredient;
		QIngredientCategory category = QIngredientCategory.ingredientCategory;
		QHqIngredient hq = QHqIngredient.hqIngredient;

		Map<Integer, IngredientItemDto> grouped = jpaQueryFactory
			    .from(ingredient)
			    .leftJoin(ingredient.category, category)
			    .leftJoin(hq).on(ingredient.id.eq(hq.ingredient.id))
			    .where(available == null ? null : ingredient.available.eq(available))
			    .groupBy(ingredient.id)
			    .transform(GroupBy.groupBy(ingredient.id).as(
			        Projections.constructor(IngredientItemDto.class,
			            ingredient.id,
			            ingredient.category.id,
			            category.name,
			            ingredient.unit,
			            ingredient.name,
			            ingredient.available,
			            hq.quantity.sum(),     // ← 이게 핵심!
			            hq.unitCost.max()      // 가격은 대표값
			        )
			    ));
		List<IngredientItemDto> content = new ArrayList<>(grouped.values());

		int offset = pageable.getPageNumber() * pageable.getPageSize();
		int limit = pageable.getPageSize();

		List<IngredientItemDto> pagedList = content.stream()
		    .skip(offset)
		    .limit(limit)
		    .collect(Collectors.toList());

		return new PageImpl<>(pagedList, pageable, content.size());
	}
}
