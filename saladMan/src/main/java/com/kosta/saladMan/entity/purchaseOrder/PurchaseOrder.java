package com.kosta.saladMan.entity.purchaseOrder;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import com.kosta.saladMan.dto.purchaseOrder.PurchaseOrderDto;
import com.kosta.saladMan.entity.store.Store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@DynamicInsert
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;  // pur_id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "store_id")
    private Store store;

    @CreationTimestamp
    private LocalDateTime orderDateTime;

    private String status; //
    
    private String orderStatus;

    private String requestedBy;

    private Integer totalPrice;

    private String purType; //발주유형

    private String qrImg;

    public PurchaseOrderDto toDto() {
        return PurchaseOrderDto.builder()
                .id(id)
                .storeId(store != null ? store.getId() : null)
                .orderDateTime(orderDateTime)
                .status(status)
                .orderStatus(status)
                .requestedBy(requestedBy)
                .totalPrice(totalPrice)
                .purType(purType)
                .qrImg(qrImg)
                .build();
    }
}
