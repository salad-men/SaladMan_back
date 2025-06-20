package com.kosta.saladMan.service.order;

import java.util.List;

import org.springframework.data.domain.Page;

import com.kosta.saladMan.dto.inventory.IngredientItemDto;
import com.kosta.saladMan.dto.purchaseOrder.LowStockItemDto;
import com.kosta.saladMan.dto.purchaseOrder.StoreOrderItemDto;

public interface OrderService {

	//본사 발주 항목 설정
    Page<IngredientItemDto> getIngredientList(Boolean available, String category, String keyword, int page, int size) throws Exception;
    Boolean toggleIngredientAvailability(Integer id) throws Exception;
    
    //매장 
    //발주 수량 미달 확인
    List<LowStockItemDto> getLowStockItems(Integer id) throws Exception;
    List<StoreOrderItemDto>getOrderItems(Integer id, String category, String keyword)throws Exception;

}
