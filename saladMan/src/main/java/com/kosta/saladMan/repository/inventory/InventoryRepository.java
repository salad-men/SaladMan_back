//package com.kosta.saladMan.repository.inventory;
//
//import com.kosta.saladMan.dto.inventory.IngredientDto;
//import com.kosta.saladMan.entity.inventory.Ingredient;
//import com.querydsl.core.BooleanBuilder;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//
//import org.modelmapper.ModelMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.stereotype.Repository;
//
//import javax.transaction.Transactional;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Repository
//public class InventoryRepository {
//
//    @Autowired
//    private JPAQueryFactory jpaQueryFactory;
//
//    @Autowired
//    private ModelMapper modelMapper;
//
//    // 재고 목록 및 검색 (페이징)
//    public List<IngredientDto> selectIngredientList(PageRequest pageRequest, String store, String category, String name) {
//        QIngredient ingredient = QIngredient.ingredient;
//        BooleanBuilder builder = new BooleanBuilder();
//
//        if (store != null && !"all".equalsIgnoreCase(store)) {
//            builder.and(ingredient.store.contains(store));
//        }
//        if (category != null && !"all".equalsIgnoreCase(category)) {
//            builder.and(ingredient.category.contains(category));
//        }
//        if (name != null && !name.trim().isEmpty()) {
//            builder.and(ingredient.name.contains(name));
//        }
//
//        List<Ingredient> ingredients = jpaQueryFactory.selectFrom(ingredient)
//                .where(builder)
//                .orderBy(ingredient.id.desc())
//                .offset(pageRequest.getOffset())
//                .limit(pageRequest.getPageSize())
//                .fetch();
//
//        return ingredients.stream()
//                .map(i -> modelMapper.map(i, IngredientDto.class))
//                .collect(Collectors.toList());
//    }
//
//    // 재고 총 개수 조회
//    public Long selectIngredientCount(String store, String category, String name) {
//        QIngredient ingredient = QIngredient.ingredient;
//        BooleanBuilder builder = new BooleanBuilder();
//
//        if (store != null && !"all".equalsIgnoreCase(store)) {
//            builder.and(ingredient.store.contains(store));
//        }
//        if (category != null && !"all".equalsIgnoreCase(category)) {
//            builder.and(ingredient.category.contains(category));
//        }
//        if (name != null && !name.trim().isEmpty()) {
//            builder.and(ingredient.name.contains(name));
//        }
//
//        return jpaQueryFactory.select(ingredient.count())
//                .from(ingredient)
//                .where(builder)
//                .fetchOne();
//    }
//
//    // 카테고리 목록 조회
//    public List<String> selectCategoryList() {
//        QIngredientCategory category = QIngredientCategory.ingredientCategory;
//        return jpaQueryFactory.select(category.name).from(category).fetch();
//    }
//
//    // 스토어 목록 조회
//    public List<String> selectStoreList() {
//        QStore store = QStore.store;
//        return jpaQueryFactory.select(store.name).from(store).fetch();
//    }
//
//    // 재고 정보 수정
//    @Transactional
//    public void updateIngredient(IngredientDto dto) {
//        QIngredient ingredient = QIngredient.ingredient;
//        jpaQueryFactory.update(ingredient)
//                .set(ingredient.name, dto.getName())
//                .set(ingredient.store, dto.getStore())
//                .set(ingredient.category, dto.getCategory())
//                .set(ingredient.stock, dto.getStock())
//                .set(ingredient.min, dto.getMin())
//                .set(ingredient.price, dto.getPrice())
//                .where(ingredient.id.eq(dto.getId()))
//                .execute();
//    }
//
//    // 재고 추가
//    @Transactional
//    public void addIngredient(IngredientDto dto) {
//        Ingredient ingredient = modelMapper.map(dto, Ingredient.class);
//        jpaQueryFactory.getEntityManager().persist(ingredient);
//    }
//}
