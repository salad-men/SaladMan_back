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
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class IngredientDslRepository {
	@Autowired
	private JPAQueryFactory jpaQueryFactory;

	@Autowired
	private ModelMapper modelMapper;

	public Page<IngredientItemDto> ingredientList(Boolean available, String category, String keyword,
			Pageable pageable) {
		QIngredient ingredient = QIngredient.ingredient;
		QIngredientCategory qcategory = QIngredientCategory.ingredientCategory;
		QHqIngredient hq = QHqIngredient.hqIngredient;

		BooleanBuilder builder = new BooleanBuilder();

		if (available != null) {
			builder.and(ingredient.available.eq(available));
		}

		if (category != null && !category.equals("all")) {
			builder.and(qcategory.name.eq(category));
		}

		if (keyword != null && !keyword.isBlank()) {
			builder.and(ingredient.name.containsIgnoreCase(keyword).or(qcategory.name.containsIgnoreCase(keyword)));
		}
		
	    Map<Integer, IngredientItemDto> grouped = jpaQueryFactory
	            .from(ingredient)
	            .leftJoin(ingredient.category, qcategory)
	            .leftJoin(hq).on(ingredient.id.eq(hq.ingredient.id))
	            .where(builder)
	            .groupBy(ingredient.id)
	            .transform(GroupBy.groupBy(ingredient.id).as(
	                Projections.constructor(IngredientItemDto.class,
	                    ingredient.id,
	                    ingredient.category.id,
	                    qcategory.name,
	                    ingredient.unit,
	                    ingredient.name,
	                    ingredient.available,
	                    hq.quantity.sum(),
	                    hq.unitCost.max()
	                )
	            ));
		
		List<IngredientItemDto> content = new ArrayList<>(grouped.values());

		int offset = pageable.getPageNumber() * pageable.getPageSize();
		int limit = pageable.getPageSize();

		List<IngredientItemDto> pagedList = content.stream().skip(offset).limit(limit).collect(Collectors.toList());

		return new PageImpl<>(pagedList, pageable, content.size());
	}
}
