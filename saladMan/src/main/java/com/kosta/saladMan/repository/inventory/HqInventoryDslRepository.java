package com.kosta.saladMan.repository.inventory;

import com.kosta.saladMan.dto.inventory.HqIngredientDto;
import com.kosta.saladMan.entity.inventory.HqIngredient;
import com.kosta.saladMan.entity.inventory.QHqIngredient;
import com.kosta.saladMan.entity.inventory.QIngredientCategory;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class HqInventoryDslRepository {

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * 본사 재고 목록 조회 (페이징 + 조건검색)
     */
    public List<HqIngredientDto> selectHqIngredientList(PageRequest pageRequest, String category, String name) {
        QHqIngredient hqIngredient = QHqIngredient.hqIngredient;
        BooleanBuilder builder = new BooleanBuilder();

        if (category != null && !"all".equalsIgnoreCase(category)) {
            builder.and(hqIngredient.category.name.contains(category));
        }
        if (name != null && !name.trim().isEmpty()) {
            builder.and(hqIngredient.ingredient.name.contains(name));
        }

        List<HqIngredient> list = jpaQueryFactory.selectFrom(hqIngredient)
                .where(builder)
                .orderBy(hqIngredient.id.desc())
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

        return list.stream()
                .map(i -> modelMapper.map(i, HqIngredientDto.class))
                .collect(Collectors.toList());
    }

    /**
     * 본사 재고 총 개수 조회 (페이징용)
     */
    public Long selectHqIngredientCount(String category, String name) {
        QHqIngredient hqIngredient = QHqIngredient.hqIngredient;
        BooleanBuilder builder = new BooleanBuilder();

        if (category != null && !"all".equalsIgnoreCase(category)) {
            builder.and(hqIngredient.category.name.contains(category));
        }
        if (name != null && !name.trim().isEmpty()) {
            builder.and(hqIngredient.ingredient.name.contains(name));
        }

        return jpaQueryFactory.select(hqIngredient.count())
                .from(hqIngredient)
                .where(builder)
                .fetchOne();
    }

    /**
     * 본사 재고 수정
     */
    @Transactional
    public void updateHqIngredient(HqIngredientDto dto) {
        QHqIngredient hqIngredient = QHqIngredient.hqIngredient;

        JPAUpdateClause clause = jpaQueryFactory.update(hqIngredient)
                .set(hqIngredient.category.id, dto.getCategoryId())
                .set(hqIngredient.ingredient.id, dto.getIngredientId())
                .set(hqIngredient.unitCost, dto.getUnitCost())
                .set(hqIngredient.expiredQuantity, dto.getExpiredQuantity())
                .set(hqIngredient.minimumOrderUnit, dto.getMinimumOrderUnit())
                .set(hqIngredient.quantity, dto.getQuantity())
                .where(hqIngredient.id.eq(dto.getId()));

        clause.execute();
    }

   
    /**
     * 본사 재고 카테고리 목록 조회
     */
    public List<String> selectCategoryList() {
        QIngredientCategory category = QIngredientCategory.ingredientCategory;
        return jpaQueryFactory.select(category.name).from(category).fetch();
    }
}
