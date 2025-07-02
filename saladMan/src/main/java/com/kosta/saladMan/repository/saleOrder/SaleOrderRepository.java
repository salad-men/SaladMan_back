package com.kosta.saladMan.repository.saleOrder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.entity.saleOrder.SaleOrder;

@Repository
public interface SaleOrderRepository extends JpaRepository<SaleOrder, Integer> {

}
