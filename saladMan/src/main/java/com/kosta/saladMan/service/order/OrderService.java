package com.kosta.saladMan.service.order;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kosta.saladMan.dto.inventory.IngredientItemDto;
import com.kosta.saladMan.dto.purchaseOrder.LowStockItemDto;
import com.kosta.saladMan.dto.purchaseOrder.PurchaseOrderDto;
import com.kosta.saladMan.dto.purchaseOrder.StoreOrderItemDto;
import com.kosta.saladMan.entity.store.Store;

public interface OrderService {

	//본사 발주 항목 설정
    Page<IngredientItemDto> getIngredientList(Boolean available, String category, String keyword, int page, int size) throws Exception;
    Boolean toggleIngredientAvailability(Integer id) throws Exception;
    //본사 발주 신청 목록 조회
    Map<String,Object> getOrderListByHq(String storeName, String status, String approval,
            LocalDate startDate, LocalDate endDate, Pageable pageable) throws Exception;
    
    
    //매장 
    //발주 수량 미달 확인
    List<LowStockItemDto> getLowStockItems(Integer id) throws Exception;
    List<StoreOrderItemDto>getOrderItems(Integer id, String category, String keyword)throws Exception;
    
    //발주 신청
    void createOrder(Store storeInfo, List<StoreOrderItemDto> items) throws Exception;
    //발주 목록
    Page<PurchaseOrderDto> getPagedOrderList(Integer storeId, String orderType, String productName,
            LocalDate startDate, LocalDate endDate, int page, int size) throws Exception;
}
