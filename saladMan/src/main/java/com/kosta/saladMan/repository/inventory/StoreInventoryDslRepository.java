package com.kosta.saladMan.repository.inventory;

import com.kosta.saladMan.dto.dashboard.InventoryExpireSummaryDto;
import com.kosta.saladMan.dto.dashboard.MainStockSummaryDto;
import com.kosta.saladMan.dto.inventory.InventoryRecordDto;
import com.kosta.saladMan.dto.inventory.StoreIngredientDto;
import com.kosta.saladMan.dto.inventory.StoreIngredientSettingDto;
import com.kosta.saladMan.entity.inventory.Disposal;
import com.kosta.saladMan.entity.inventory.QDisposal;
import com.kosta.saladMan.entity.inventory.QHqIngredient;
import com.kosta.saladMan.entity.inventory.QIngredient;
import com.kosta.saladMan.entity.inventory.QIngredientCategory;
import com.kosta.saladMan.entity.inventory.QInventoryRecord;
import com.kosta.saladMan.entity.inventory.QStoreIngredient;
import com.kosta.saladMan.entity.inventory.QStoreIngredientSetting;
import com.kosta.saladMan.entity.purchaseOrder.QPurchaseOrder;
import com.kosta.saladMan.entity.purchaseOrder.QPurchaseOrderItem;
import com.kosta.saladMan.entity.store.QStore;
import com.querydsl.core.BooleanBuilder;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.querydsl.core.types.OrderSpecifier;

@Repository
public class StoreInventoryDslRepository {

    @Autowired
    private JPAQueryFactory queryFactory;

    // 매장 재고 조회
    public List<StoreIngredientDto> findStoreInventoryByFilters(
            Integer storeId, Integer categoryId, String keyword,
            LocalDate startDate, LocalDate endDate, PageRequest pageRequest, String sortOption) {

        QStoreIngredient q = QStoreIngredient.storeIngredient;
        QStore store = QStore.store;
        QStoreIngredientSetting s = QStoreIngredientSetting.storeIngredientSetting;
        QHqIngredient hq = QHqIngredient.hqIngredient;

        BooleanBuilder builder = new BooleanBuilder();
        if (storeId != null) builder.and(q.store.id.eq(storeId));
        if (categoryId != null) builder.and(q.category.id.eq(categoryId));
        if (keyword != null && !keyword.isBlank()) builder.and(q.ingredient.name.containsIgnoreCase(keyword));
        if (startDate != null) builder.and(q.expiredDate.goe(startDate));
        if (endDate != null) builder.and(q.expiredDate.loe(endDate));
        builder.and(q.quantity.gt(0));

        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        // 정렬 옵션 세팅
        if ("receivedAsc".equals(sortOption)) {
            orderSpecifiers.add(q.receivedDate.asc().nullsLast());
            orderSpecifiers.add(q.category.name.asc());
            orderSpecifiers.add(q.ingredient.name.asc());
        } else if ("receivedDesc".equals(sortOption)) {
            orderSpecifiers.add(q.receivedDate.desc().nullsLast());
            orderSpecifiers.add(q.category.name.asc());
            orderSpecifiers.add(q.ingredient.name.asc());
        } else if ("expiryAsc".equals(sortOption)) {
            orderSpecifiers.add(q.expiredDate.asc().nullsLast());
            orderSpecifiers.add(q.category.name.asc());
            orderSpecifiers.add(q.ingredient.name.asc());
        } else if ("expiryDesc".equals(sortOption)) {
            orderSpecifiers.add(q.expiredDate.desc().nullsLast());
            orderSpecifiers.add(q.category.name.asc());
            orderSpecifiers.add(q.ingredient.name.asc());
        } else {
            orderSpecifiers.add(q.category.name.asc());
            orderSpecifiers.add(q.ingredient.name.asc());
            orderSpecifiers.add(q.expiredDate.asc().nullsLast());
        }

        
        List<StoreIngredientDto> list = queryFactory
                .select(Projections.bean(
                        StoreIngredientDto.class,
                        q.id,
                        q.quantity,
                        q.expiredDate,
                        q.receivedDate,
                        q.ingredient.name.as("ingredientName"),
                        q.ingredient.id.as("ingredientId"),
                        q.ingredient.unit.as("unit"),
                        q.category.name.as("categoryName"),
                        q.category.id.as("categoryId"), 
                        store.name.as("storeName"),
                        s.minQuantity.as("minQuantity"),
                        hq.unitCost.as("unitCost"),
                        hq.minimumOrderUnit.as("minimumOrderUnit")
                ))
                .from(q)
                .leftJoin(q.ingredient)
                .leftJoin(q.category)
                .leftJoin(q.store, store)
                .leftJoin(s).on(s.store.id.eq(store.id).and(s.ingredient.eq(q.ingredient)))
                .leftJoin(hq).on(hq.ingredient.eq(q.ingredient).and(hq.store.id.eq(1)))
                .where(builder)
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

            // 중복 id 제거
            Map<Integer, StoreIngredientDto> map = new LinkedHashMap<>();
            for (StoreIngredientDto dto : list) {
                map.put(dto.getId(), dto);
            }
            return new ArrayList<>(map.values());
    }

    
    //매장 재고 개수 조회
    public long countStoreInventoryByFilters(
            Integer storeId, Integer categoryId, String keyword,
            LocalDate startDate, LocalDate endDate) {

        QStoreIngredient q = QStoreIngredient.storeIngredient;
        BooleanBuilder builder = new BooleanBuilder();

        if (storeId != null) {
            builder.and(q.store.id.eq(storeId));
        }
        if (categoryId != null) {
            builder.and(q.category.id.eq(categoryId));
        }
        if (keyword != null && !keyword.isBlank()) {
            builder.and(q.ingredient.name.containsIgnoreCase(keyword));
        }
        if (startDate != null) builder.and(q.expiredDate.goe(startDate));
        if (endDate != null) builder.and(q.expiredDate.loe(endDate));

        Long count = queryFactory.select(q.count()).from(q).where(builder).fetchOne();

        return count == null ? 0L : count;
    }


    // 매장 재고 업데이트
    @Transactional
    public void updateStoreIngredient(StoreIngredientDto dto) {
        QStoreIngredient q = QStoreIngredient.storeIngredient;
        JPAUpdateClause clause = queryFactory.update(q)
                .set(q.quantity, dto.getQuantity())
                .set(q.expiredDate, dto.getExpiredDate())
                .where(q.id.eq(dto.getId()));
        clause.execute();
    }

 // 폐기 개수 조회 (keyword 포함)
    public int countStoreDisposals(Integer storeId, Integer categoryId, String status,
                                  LocalDate startDate, LocalDate endDate, String keyword) {
        QDisposal d = QDisposal.disposal;
        BooleanBuilder builder = new BooleanBuilder();

        if (storeId != null) builder.and(d.store.id.eq(storeId));
        if (categoryId != null) builder.and(d.ingredient.category.id.eq(categoryId));
        if (status != null && !"all".equals(status)) builder.and(d.status.eq(status));
        if (startDate != null) builder.and(d.requestedAt.goe(startDate));
        if (endDate != null) builder.and(d.requestedAt.loe(endDate));
        if (keyword != null && !keyword.isBlank()) {
            builder.and(d.ingredient.name.containsIgnoreCase(keyword));
        }

        Long cnt = queryFactory.select(d.count()).from(d).where(builder).fetchOne();
        return cnt == null ? 0 : cnt.intValue();
    }


    // 폐기목록 조회
    public List<Disposal> selectStoreDisposalListByFiltersPaging(
            Integer storeId, Integer categoryId, String status,
            LocalDate startDate, LocalDate endDate,
            int offset, int limit,
            String sortOption, String keyword) {
        
        QDisposal d = QDisposal.disposal;
        BooleanBuilder builder = new BooleanBuilder();

        if (storeId != null) builder.and(d.store.id.eq(storeId));
        if (categoryId != null) builder.and(d.ingredient.category.id.eq(categoryId));
        if (status != null && !"all".equals(status)) builder.and(d.status.eq(status));
        if (startDate != null) builder.and(d.requestedAt.goe(startDate));
        if (endDate != null) builder.and(d.requestedAt.loe(endDate));
        if (keyword != null && !keyword.isBlank()) {
            builder.and(d.ingredient.name.containsIgnoreCase(keyword));
        }

        List<OrderSpecifier<?>> orders = new ArrayList<>();
        switch (sortOption) {
            case "dateAsc":
                orders.add(d.requestedAt.asc());
                break;
            case "dateDesc":
                orders.add(d.requestedAt.desc());
                break;
            default:
                orders.add(d.ingredient.category.name.asc());
                orders.add(d.ingredient.name.asc());
                orders.add(d.requestedAt.desc());
        }

        return queryFactory
                .selectFrom(d)
                .where(builder)
                .orderBy(orders.toArray(new OrderSpecifier[0]))
                .offset(offset)
                .limit(limit)
                .fetch();
    }

 // 폐기 개수 조회 (여러 매장)
    public int countStoreDisposalsForStores(
            List<Integer> storeIds, Integer categoryId, String status,
            LocalDate startDate, LocalDate endDate, String keyword) {

        QDisposal d = QDisposal.disposal;
        BooleanBuilder builder = new BooleanBuilder();

        if (storeIds != null && !storeIds.isEmpty()) {
            builder.and(d.store.id.in(storeIds));
        }
        if (categoryId != null) {
            builder.and(d.ingredient.category.id.eq(categoryId));
        }
        if (status != null && !"all".equals(status)) {
            builder.and(d.status.eq(status));
        }
        if (startDate != null) {
            builder.and(d.requestedAt.goe(startDate));
        }
        if (endDate != null) {
            builder.and(d.requestedAt.loe(endDate));
        }
        if (keyword != null && !keyword.isBlank()) {
            builder.and(d.ingredient.name.containsIgnoreCase(keyword));
        }

        Long cnt = queryFactory.select(d.count()).from(d).where(builder).fetchOne();
        return cnt == null ? 0 : cnt.intValue();
    }

    // 폐기목록 조회 (여러 매장)
    public List<Disposal> selectStoreDisposalListByFiltersPagingForStores(
            List<Integer> storeIds, Integer categoryId, String status,
            LocalDate startDate, LocalDate endDate,
            int offset, int limit,
            String sortOption, String keyword) {

        QDisposal d = QDisposal.disposal;
        BooleanBuilder builder = new BooleanBuilder();

        if (storeIds != null && !storeIds.isEmpty()) {
            builder.and(d.store.id.in(storeIds));
        }
        if (categoryId != null) {
            builder.and(d.ingredient.category.id.eq(categoryId));
        }
        if (status != null && !"all".equals(status)) {
            builder.and(d.status.eq(status));
        }
        if (startDate != null) {
            builder.and(d.requestedAt.goe(startDate));
        }
        if (endDate != null) {
            builder.and(d.requestedAt.loe(endDate));
        }
        if (keyword != null && !keyword.isBlank()) {
            builder.and(d.ingredient.name.containsIgnoreCase(keyword));
        }

        List<OrderSpecifier<?>> orders = new ArrayList<>();
        switch (sortOption) {
            case "dateAsc":
                orders.add(d.requestedAt.asc());
                break;
            case "dateDesc":
                orders.add(d.requestedAt.desc());
                break;
            default:
                orders.add(d.ingredient.category.name.asc());
                orders.add(d.ingredient.name.asc());
                orders.add(d.requestedAt.desc());
        }

        return queryFactory
                .selectFrom(d)
                .where(builder)
                .orderBy(orders.toArray(new OrderSpecifier[0]))
                .offset(offset)
                .limit(limit)
                .fetch();
    }


    //매장 재고 기록 조회
    public List<InventoryRecordDto> findStoreInventoryRecords(Integer storeId, String changeType, PageRequest pageRequest) {
        QInventoryRecord ir = QInventoryRecord.inventoryRecord;
        QIngredient ingredient = QIngredient.ingredient;
        QIngredientCategory category = QIngredientCategory.ingredientCategory;
        QStore store = QStore.store;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(ir.store.id.eq(storeId));
        if (changeType != null && !changeType.isBlank()) {
            builder.and(ir.changeType.eq(changeType));
        }

        return queryFactory
            .select(Projections.bean(
                InventoryRecordDto.class,
                ir.id,
                ir.quantity,
                ir.changeType,
                ir.memo,
                ir.date,
                ingredient.id.as("ingredientId"),
                ingredient.name.as("ingredientName"),
                category.name.as("categoryName"),
                store.id.as("storeId"),
                store.name.as("storeName")
            ))
            .from(ir)
            .leftJoin(ir.ingredient, ingredient)
            .leftJoin(ingredient.category, category)
            .leftJoin(ir.store, store)
            .where(builder)
            .orderBy(ir.date.desc())
            .offset(pageRequest.getOffset())
            .limit(pageRequest.getPageSize())
            .fetch();
    }

    //매장 설정 개수 조회
    public long countStoreSettingsByFilters(Integer storeId, Integer categoryId, String keyword) {
        QStoreIngredientSetting setting = QStoreIngredientSetting.storeIngredientSetting;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(setting.store.id.eq(storeId));
        if (categoryId != null) builder.and(setting.ingredient.category.id.eq(categoryId));
        if (keyword != null && !keyword.isEmpty())
            builder.and(setting.ingredient.name.containsIgnoreCase(keyword));

        Long count = queryFactory.select(setting.count()).from(setting).where(builder).fetchOne();
        return count == null ? 0 : count;
    }
    //매장 설정 조회
    public List<StoreIngredientSettingDto> findStoreSettingsByFilters(
            Integer storeId, Integer categoryId, String keyword, int offset, int limit) {
        QStoreIngredientSetting setting = QStoreIngredientSetting.storeIngredientSetting;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(setting.store.id.eq(storeId));
        if (categoryId != null) builder.and(setting.ingredient.category.id.eq(categoryId));
        if (keyword != null && !keyword.isEmpty())
            builder.and(setting.ingredient.name.containsIgnoreCase(keyword));

        return queryFactory
                .select(Projections.bean(StoreIngredientSettingDto.class,
                        setting.id,
                        setting.minQuantity,
                        setting.maxQuantity,
                        setting.ingredient.id.as("ingredientId"),
                        setting.ingredient.category.name.as("categoryName")
                ))
                .from(setting)
                .where(builder)
                .offset(offset)
                .limit(limit)
                .fetch();
    }
    
    
    
    
    public List<MainStockSummaryDto> findTopUsedIngredients(
            Integer storeId, LocalDate fromDate, int limit) {
        QInventoryRecord ir = QInventoryRecord.inventoryRecord;
        QIngredient i = QIngredient.ingredient;
        QIngredientCategory c = QIngredientCategory.ingredientCategory;

        // 판매로 인한 출고(changeType = '사용') 집계
        return queryFactory
            .select(Projections.constructor(MainStockSummaryDto.class,
                    ir.ingredient.id,
                    i.name,
                    c.name,
                    ir.quantity.sum(),   // 주의: 출고라면 음수 합계가 나올 수 있음
                    i.unit
            ))
            .from(ir)
            .join(ir.ingredient, i)
            .join(i.category, c)
            .where(
                ir.store.id.eq(storeId),
                ir.changeType.eq("사용"),
                ir.date.goe(fromDate.atStartOfDay())
            )
            .groupBy(ir.ingredient.id, i.name, c.name, i.unit)
            .orderBy(ir.quantity.sum().abs().desc()) // 출고량 큰 순(절대값)
            .limit(limit)
            .fetch();
    }
    
    public int countAutoOrderExpectedItems(Integer storeId) {
        QStoreIngredientSetting setting = QStoreIngredientSetting.storeIngredientSetting;
        QStoreIngredient stock = QStoreIngredient.storeIngredient;
        QPurchaseOrder order = QPurchaseOrder.purchaseOrder;
        QPurchaseOrderItem orderItem = QPurchaseOrderItem.purchaseOrderItem;

        // 현재 보유 재고 + 입고 대기중 수량
        // (최소수량 > (재고 + 입고대기)) 인 품목의 개수

        Long count = queryFactory
                .select(setting.count())
                .from(setting)
                // 실제 보유 재고와 연결(없으면 0)
                .leftJoin(stock).on(
                    setting.store.id.eq(stock.store.id)
                    .and(setting.ingredient.id.eq(stock.ingredient.id))
                )
                .where(
                    setting.store.id.eq(storeId),
                    // 자동발주 세팅이 되어있는 품목만 (autoOrderEnabled == true)
                    // FixedOrderItem 쪽에 세팅이 있을 때만 카운트하는게 더 정확, 아니면 모든 세팅 품목 대상으로 체크
                    // 아래는 StoreIngredientSetting 기준, 필요하면 FixedOrderItem 쿼리로도 가능

                    // 최소수량 > (재고 + 입고대기)
                    setting.minQuantity.gt(
                        stock.quantity.coalesce(0)
                            .add(
                                // 입고 대기중(대기중 상태)인 주문의 미입고 수량 합계
                                JPAExpressions
                                    .select(orderItem.orderedQuantity.subtract(orderItem.receivedQuantity).sum().coalesce(0))
                                    .from(orderItem)
                                    .join(orderItem.purchaseOrder, order)
                                    .where(
                                        order.store.id.eq(storeId),
                                        orderItem.ingredient.id.eq(setting.ingredient.id),
                                        order.status.in("대기중") // 입고 전 상태
                                    )
                            )
                    )
                )
                .fetchOne();

        return count != null ? count.intValue() : 0;
    }
    
 // InventoryExpireSummaryDto는 기존처럼 Item List, 건수, D-DAY, D-1 포함 DTO 설계
    public InventoryExpireSummaryDto getExpireSummaryTop3WithCountMerged(String startDate, String endDate, Integer storeId) {
        QStoreIngredient q = QStoreIngredient.storeIngredient;
        LocalDate sDate = (startDate != null && !startDate.isBlank()) ? LocalDate.parse(startDate) : null;
        LocalDate eDate = (endDate != null && !endDate.isBlank()) ? LocalDate.parse(endDate) : null;

        // Top3
        List<InventoryExpireSummaryDto.Item> top3 = queryFactory
            .select(Projections.bean(
                InventoryExpireSummaryDto.Item.class,
                q.ingredient.name.as("ingredientName"),
                q.category.name.as("categoryName"),
                q.quantity.as("remainQuantity"),
                q.expiredDate.stringValue().as("expiredDate")
            ))
            .from(q)
            .where(
                q.store.id.eq(storeId),
                q.quantity.gt(0),
                sDate != null ? q.expiredDate.goe(sDate) : null,
                eDate != null ? q.expiredDate.loe(eDate) : null
            )
            .orderBy(q.expiredDate.asc(), q.quantity.desc())
            .limit(3)
            .fetch();

        // 전체 임박재고 건수
        Long totalCount = queryFactory
            .select(q.count())
            .from(q)
            .where(
                q.store.id.eq(storeId),
                q.quantity.gt(0),
                sDate != null ? q.expiredDate.goe(sDate) : null,
                eDate != null ? q.expiredDate.loe(eDate) : null
            )
            .fetchOne();

        // D-1, D-DAY
        LocalDate today = LocalDate.now();
        Long d1Count = queryFactory.select(q.count()).from(q)
            .where(q.store.id.eq(storeId), q.quantity.gt(0), q.expiredDate.eq(today.plusDays(1))).fetchOne();
        Long todayCount = queryFactory.select(q.count()).from(q)
            .where(q.store.id.eq(storeId), q.quantity.gt(0), q.expiredDate.eq(today)).fetchOne();

        InventoryExpireSummaryDto dto = new InventoryExpireSummaryDto();
        dto.setTop3(top3);
        dto.setTotalCount(totalCount != null ? totalCount.intValue() : 0);
        dto.setD1Count(d1Count != null ? d1Count.intValue() : 0);
        dto.setTodayCount(todayCount != null ? todayCount.intValue() : 0);
        return dto;
    }
    
    // 임박재고 Top3+전체건수+임박카운트
    public InventoryExpireSummaryDto findExpireSummaryTop3WithCountMerged(String startDate, String endDate) {
        QStoreIngredient si = QStoreIngredient.storeIngredient;

        LocalDate sDate = (startDate != null && !startDate.isBlank()) ? LocalDate.parse(startDate) : null;
        LocalDate eDate = (endDate != null && !endDate.isBlank()) ? LocalDate.parse(endDate) : null;

        // Top3 임박재고
        List<InventoryExpireSummaryDto.Item> top3 = queryFactory
            .select(Projections.bean(
                InventoryExpireSummaryDto.Item.class,
                si.ingredient.name.as("ingredientName"),
                si.category.name.as("categoryName"),
                si.quantity.as("remainQuantity"),
                si.expiredDate.stringValue().as("expiredDate")
            ))
            .from(si)
            .where(
                si.quantity.gt(0),
                sDate != null ? si.expiredDate.goe(sDate) : null,
                eDate != null ? si.expiredDate.loe(eDate) : null
            )
            .orderBy(si.expiredDate.asc(), si.quantity.desc())
            .limit(3)
            .fetch();

        // 전체 임박재고 건수
        Long totalCount = queryFactory
            .select(si.count())
            .from(si)
            .where(
                si.quantity.gt(0),
                sDate != null ? si.expiredDate.goe(sDate) : null,
                eDate != null ? si.expiredDate.loe(eDate) : null
            )
            .fetchOne();

        LocalDate today = LocalDate.now();

        // D-1 (내일 유통기한)
        Long d1Count = queryFactory
            .select(si.count())
            .from(si)
            .where(
                si.quantity.gt(0),
                si.expiredDate.eq(today.plusDays(1))
            )
            .fetchOne();

        // D-DAY (오늘 유통기한)
        Long todayCount = queryFactory
            .select(si.count())
            .from(si)
            .where(
                si.quantity.gt(0),
                si.expiredDate.eq(today)
            )
            .fetchOne();

        InventoryExpireSummaryDto dto = new InventoryExpireSummaryDto();
        dto.setTop3(top3);
        dto.setTotalCount(totalCount != null ? totalCount.intValue() : 0);
        dto.setD1Count(d1Count != null ? d1Count.intValue() : 0);
        dto.setTodayCount(todayCount != null ? todayCount.intValue() : 0);

        return dto;
    }

    // 자동발주 예정 품목 카운트
    public int countAutoOrderExpectedByStore(Integer storeId) {
        QStoreIngredientSetting setting = QStoreIngredientSetting.storeIngredientSetting;
        QStoreIngredient stock = QStoreIngredient.storeIngredient;
        QPurchaseOrder order = QPurchaseOrder.purchaseOrder;
        QPurchaseOrderItem orderItem = QPurchaseOrderItem.purchaseOrderItem;

        Long count = queryFactory
                .select(setting.count())
                .from(setting)
                .leftJoin(stock).on(
                        setting.store.id.eq(stock.store.id)
                        .and(setting.ingredient.id.eq(stock.ingredient.id))
                )
                .where(
                        setting.store.id.eq(storeId),
                        setting.minQuantity.gt(
                                stock.quantity.coalesce(0)
                                        .add(
                                                JPAExpressions
                                                        .select(orderItem.orderedQuantity.subtract(orderItem.receivedQuantity).sum().coalesce(0))
                                                        .from(orderItem)
                                                        .join(orderItem.purchaseOrder, order)
                                                        .where(
                                                                order.store.id.eq(storeId),
                                                                orderItem.ingredient.id.eq(setting.ingredient.id),
                                                                order.status.in("대기중")
                                                        )
                                        )
                        )
                )
                .fetchOne();

        return count != null ? count.intValue() : 0;
    }
    
 // 2. 매장 임박재고 Top3 + 전체건수 + D-1, D-day 개수 (DTO로 반환)
    public InventoryExpireSummaryDto findExpireSummaryTop3WithCountMergedByStore(
            Integer storeId, String startDate, String endDate) {

        QStoreIngredient q = QStoreIngredient.storeIngredient;
        LocalDate sDate = (startDate != null && !startDate.isBlank()) ? LocalDate.parse(startDate) : null;
        LocalDate eDate = (endDate != null && !endDate.isBlank()) ? LocalDate.parse(endDate) : null;

        // Top3
        List<InventoryExpireSummaryDto.Item> top3 = queryFactory
                .select(Projections.bean(
                        InventoryExpireSummaryDto.Item.class,
                        q.ingredient.name.as("ingredientName"),
                        q.category.name.as("categoryName"),
                        q.quantity.as("remainQuantity"),
                        q.expiredDate.stringValue().as("expiredDate")
                ))
                .from(q)
                .where(
                        q.store.id.eq(storeId),
                        q.quantity.gt(0),
                        sDate != null ? q.expiredDate.goe(sDate) : null,
                        eDate != null ? q.expiredDate.loe(eDate) : null
                )
                .orderBy(q.expiredDate.asc(), q.quantity.desc())
                .limit(3)
                .fetch();

        // 전체 임박재고 건수
        Long totalCount = queryFactory
                .select(q.count())
                .from(q)
                .where(
                        q.store.id.eq(storeId),
                        q.quantity.gt(0),
                        sDate != null ? q.expiredDate.goe(sDate) : null,
                        eDate != null ? q.expiredDate.loe(eDate) : null
                )
                .fetchOne();

        LocalDate today = LocalDate.now();
        Long d1Count = queryFactory.select(q.count()).from(q)
                .where(q.store.id.eq(storeId), q.quantity.gt(0), q.expiredDate.eq(today.plusDays(1))).fetchOne();
        Long todayCount = queryFactory.select(q.count()).from(q)
                .where(q.store.id.eq(storeId), q.quantity.gt(0), q.expiredDate.eq(today)).fetchOne();

        InventoryExpireSummaryDto dto = new InventoryExpireSummaryDto();
        dto.setTop3(top3);
        dto.setTotalCount(totalCount != null ? totalCount.intValue() : 0);
        dto.setD1Count(d1Count != null ? d1Count.intValue() : 0);
        dto.setTodayCount(todayCount != null ? todayCount.intValue() : 0);

        return dto;
    }

    // 3. 최근 한달간 가장 많이 사용한 재고 Top5 (매장별)
    public List<MainStockSummaryDto> findMainStocksByStoreForMonth(Integer storeId) {
        QInventoryRecord ir = QInventoryRecord.inventoryRecord;
        QIngredient i = QIngredient.ingredient;
        QIngredientCategory c = QIngredientCategory.ingredientCategory;

        LocalDate monthAgo = LocalDate.now().minusDays(30);

        return queryFactory
                .select(Projections.constructor(MainStockSummaryDto.class,
                        ir.ingredient.id,
                        i.name,
                        c.name,
                        ir.quantity.sum(),
                        i.unit
                ))
                .from(ir)
                .join(ir.ingredient, i)
                .join(i.category, c)
                .where(
                        ir.store.id.eq(storeId),
                        ir.changeType.eq("사용"),
                        ir.date.goe(monthAgo.atStartOfDay())
                )
                .groupBy(ir.ingredient.id, i.name, c.name, i.unit)
                .orderBy(ir.quantity.sum().abs().desc())
                .limit(5)
                .fetch();
    }
}
