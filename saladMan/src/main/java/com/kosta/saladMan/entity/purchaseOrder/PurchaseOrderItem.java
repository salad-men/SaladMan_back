package com.kosta.saladMan.entity.purchaseOrder;



import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.DynamicInsert;

import com.kosta.saladMan.entity.inventory.Ingredient;

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
public class PurchaseOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;  // pur_detail_id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "purchase_order_id")
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "ingredient_id")
    private Ingredient ingredient;

    private Integer orderedQuantity;

    private Integer receivedQuantity;

    private Integer totalPrice;

    @Column(length = 255)
    private String inspection;

    @Column(length = 255)
    private String inspectionNote;
}
