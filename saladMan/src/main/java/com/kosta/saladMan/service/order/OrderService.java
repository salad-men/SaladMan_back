package com.kosta.saladMan.service.order;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kosta.saladMan.dto.inventory.IngredientCategoryDto;
import com.kosta.saladMan.dto.inventory.IngredientItemDto;
import com.kosta.saladMan.dto.purchaseOrder.FixedOrderItemDto;
import com.kosta.saladMan.dto.purchaseOrder.LowStockItemDto;
import com.kosta.saladMan.dto.purchaseOrder.PurchaseOrderDto;
import com.kosta.saladMan.dto.purchaseOrder.PurchaseOrderItemDto;
import com.kosta.saladMan.dto.purchaseOrder.StoreOrderItemDto;
import com.kosta.saladMan.entity.store.Store;

public interface OrderService {

	//본사 재료 리스트
    Page<IngredientItemDto> getIngredientList(Boolean available, String category, String keyword, int page, int size) throws Exception;
    //발주 신청 가능 항목 설정
    Boolean toggleIngredientAvailability(Integer id) throws Exception;
    //본사 발주 신청 목록 조회
    Map<String,Object> getOrderListByHq(String storeName, String status, LocalDate startDate, LocalDate endDate, Pageable pageable) throws Exception;
    //본사 발주 신청 상세
    List<PurchaseOrderItemDto> getOrderDetailByHq(Integer purchaseOrderId) throws Exception;
    //본사 발주 신청 수락
    void updateOrderItems(List<PurchaseOrderItemDto> items) throws Exception;
    
    
    //----------------------매장---------------------------- 
   
    //발주 수량 미달 확인
    List<LowStockItemDto> getLowStockItems(Integer id) throws Exception;
    List<StoreOrderItemDto>getOrderItems(Integer id, String category, String keyword)throws Exception;
    
    //발주 신청
    void createOrder(Store storeInfo, List<StoreOrderItemDto> items,String purchaseType) throws Exception;
    //발주 목록
    Page<PurchaseOrderDto> getPagedOrderList(Integer storeId, String orderType, String productName,
            LocalDate startDate, LocalDate endDate, int page, int size) throws Exception;
    
    //입고검수 페이지 조회
    List<PurchaseOrderItemDto> getInspectionInfo(Integer orderId) throws Exception;
    //입고 검수 페이지 저장
    void saveInspectionResults(List<PurchaseOrderItemDto> items) throws Exception;

    //발주 설정 조회
    List<FixedOrderItemDto> getSettings(Integer id) throws Exception;
    //발주 설정 저장
    void saveSettings(Integer id, List<FixedOrderItemDto> settingList) throws Exception;

    Boolean getAutoOrderEnabled(Integer id) throws Exception;
    void updateAutoOrderEnabled(Integer id, Boolean enable) throws Exception;

    
    //-----------------------공통---------------------------
    List<IngredientCategoryDto> getAllIngredientCategory() throws Exception;
} 



