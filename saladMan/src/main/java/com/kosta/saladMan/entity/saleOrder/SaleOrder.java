package com.kosta.saladMan.entity.saleOrder;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Column;
import javax.persistence.FetchType;

import org.hibernate.annotations.DynamicInsert;

import com.kosta.saladMan.entity.store.Store;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@DynamicInsert
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "store_id")
    private Store store;

    @Column(nullable = false)
    private LocalDateTime orderTime;

    private String status;

    private Integer totalPrice;
}
