package com.kosta.saladMan.repository.saleOrder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.dto.saleOrder.StoreSalesResultDto;
import com.kosta.saladMan.dto.saleOrder.PaymentListDto;
import com.kosta.saladMan.dto.saleOrder.SalesResultDto;
import com.kosta.saladMan.dto.saleOrder.SalesResultDto.DailySalesDto;
import com.kosta.saladMan.dto.saleOrder.SalesResultDto.GroupType;
import com.kosta.saladMan.entity.menu.QTotalMenu;
import com.kosta.saladMan.entity.saleOrder.QSaleOrder;
import com.kosta.saladMan.entity.saleOrder.QSaleOrderItem;
import com.kosta.saladMan.util.PageInfo;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class SalesDslRepository {
	
	@Autowired
	private JPAQueryFactory jpaQueryFactory;
	
	//전체or매장 매출조회
	public List<DailySalesDto> getStoreSales(Integer storeId, LocalDateTime start, LocalDateTime end, GroupType groupType) {
	    return getGroupedSales(storeId, start, end, groupType);
	}
	public List<DailySalesDto> getTotalSales(LocalDateTime start, LocalDateTime end, GroupType groupType) {
	    return getGroupedSales(null, start, end, groupType);
	}

	public List<DailySalesDto> getGroupedSales(Integer storeId, LocalDateTime start, LocalDateTime end, GroupType groupType) {
	    QSaleOrder saleOrder = QSaleOrder.saleOrder;
	    QSaleOrderItem item = QSaleOrderItem.saleOrderItem;

	    Expression<String> groupExpr;
	    switch (groupType) {
	        case WEEK:
	            groupExpr = Expressions.stringTemplate("DATE_FORMAT({0}, '%x-%v')", saleOrder.orderTime);
	            break;
	        case MONTH:
	            groupExpr = Expressions.stringTemplate("DATE_FORMAT({0}, '%Y-%m')", saleOrder.orderTime);
	            break;
	        case DAY:
	        default:
	        	groupExpr = Expressions.stringTemplate("DATE_FORMAT({0}, '%Y-%m-%d')", saleOrder.orderTime);
	            break;
	    }
	    
	    return jpaQueryFactory.select(Projections.fields(
	                DailySalesDto.class,
	                ExpressionUtils.as(groupExpr, "date"),
	                item.quantity.sum().intValue().as("quantity"),
	                item.price.sum().intValue().as("revenue")
	            ))
	            .from(item)
	            .join(item.saleOrder, saleOrder)
	            .where(saleOrder.orderTime.between(start, end)
	            		.and(storeId != null ? saleOrder.store.id.eq(storeId) : null))
	            .groupBy(groupExpr)
	            .orderBy(new OrderSpecifier(Order.ASC, groupExpr))
	            .fetch();
	}
	
	// 메뉴별 매출조회
	public List<SalesResultDto.MenuSalesDto> getStoreMenuSales(Integer storeId, LocalDateTime start, LocalDateTime end) {
	    return getMenuSales(storeId, start, end);
	}
	public List<SalesResultDto.MenuSalesDto> getTotalMenuSales(LocalDateTime start, LocalDateTime end) {
	    return getMenuSales(null, start, end);
	}

    public List<SalesResultDto.MenuSalesDto> getMenuSales(Integer storeId, LocalDateTime start, LocalDateTime end) {
        QSaleOrder saleOrder = QSaleOrder.saleOrder;
        QSaleOrderItem item = QSaleOrderItem.saleOrderItem;
        QTotalMenu menu = QTotalMenu.totalMenu;

        return jpaQueryFactory.select(Projections.fields(
        		SalesResultDto.MenuSalesDto.class,
                    menu.name.as("menuName"),
                    item.quantity.sum().intValue().as("quantity")
                ))
                .from(item, saleOrder, menu)
                .where(
                		item.saleOrder.id.eq(saleOrder.id),
                        item.menuId.eq(menu.id),
                        saleOrder.orderTime.between(start, end)
                        .and(storeId != null ? saleOrder.store.id.eq(storeId) : null)
                )
                .groupBy(menu.name)
                .orderBy(item.quantity.sum().desc())
                .fetch();
    }
    
    public Page<PaymentListDto> getPaymentList(Integer storeId, String status, String start, String end, PageRequest pageRequest) {
        QSaleOrder so = QSaleOrder.saleOrder;
        QSaleOrderItem soi = QSaleOrderItem.saleOrderItem;
        QTotalMenu tm = QTotalMenu.totalMenu;

        BooleanBuilder builder = new BooleanBuilder();
        if (storeId != null) {
            builder.and(so.store.id.eq(storeId));
        }
        if (status != null) {
            builder.and(so.status.eq(status));
        }
        if (start != null) {
        	LocalDate startDate = LocalDate.parse(start);
            builder.and(so.orderTime.goe(startDate.atStartOfDay()));
        }
        if (end != null) {
        	LocalDate endDate = LocalDate.parse(end);
            builder.and(so.orderTime.loe(endDate.atTime(23,59,59)));
        }

        List<PaymentListDto> content = jpaQueryFactory
            .select(Projections.bean(PaymentListDto.class,
            		so.id,
            		so.orderTime,
            		so.status,
            		so.totalPrice,
            		so.store,
            		soi.menuId,
            		soi.quantity,
            		tm.name
            ))
            .from(so)
            .join(soi).on(so.id.eq(soi.saleOrder.id))
            .join(tm).on(soi.menuId.eq(tm.id))
            .where(builder)
            .orderBy(so.orderTime.desc())
            .offset(pageRequest.getOffset())
            .limit(pageRequest.getPageSize())
            .fetch();

        long total = jpaQueryFactory
        	    .select(so.count())
        	    .from(so)
        	    .join(soi).on(so.id.eq(soi.saleOrder.id))
        	    .join(tm).on(soi.menuId.eq(tm.id))
        	    .where(builder)
        	    .fetchOne(); 

        return new PageImpl<>(content, pageRequest, total);
    }   
    
}
