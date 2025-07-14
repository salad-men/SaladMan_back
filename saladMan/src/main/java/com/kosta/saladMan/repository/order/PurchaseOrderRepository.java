package com.kosta.saladMan.repository.order;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.entity.purchaseOrder.PurchaseOrder;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Integer> {

    int countByStoreIdAndOrderStatusAndOrderDateTimeBetween(
            Integer storeId,
            String orderStatus,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
        );

}
