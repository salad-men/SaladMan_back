package com.kosta.saladMan.entity.purchaseOrder;

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
public class FixedOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "ingredient_id")
    private Ingredient ingredient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "fixed_order_template_id")
    private FixedOrderTemplate fixedOrderTemplate;

    private Integer quantity;
}
