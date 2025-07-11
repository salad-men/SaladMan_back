package com.kosta.saladMan.repository.saleOrder;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.entity.saleOrder.SaleOrder;
import com.kosta.saladMan.entity.saleOrder.SaleOrderItem;

@Repository
public interface SaleOrderItemRepository extends JpaRepository<SaleOrderItem, Integer> {
    List<SaleOrderItem> findBySaleOrder(SaleOrder saleOrder);

}
