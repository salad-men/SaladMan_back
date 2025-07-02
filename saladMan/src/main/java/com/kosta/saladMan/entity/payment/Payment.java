package com.kosta.saladMan.entity.payment;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import com.kosta.saladMan.entity.menu.MenuCategory;
import com.kosta.saladMan.entity.menu.TotalMenu;
import com.kosta.saladMan.entity.saleOrder.SaleOrder;

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
public class Payment {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;

	    // 주문과 1:1 연결
	    @OneToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "order_id", nullable = false, unique = true)
	    private SaleOrder saleOrder;

	    @Column(length = 50, unique = true)
	    private String externalOrderId; // 토스용
	    
	    // 토스 결제 키
	    @Column(length = 100)
	    private String paymentKey;

	    // 결제 상태 (READY, PAID, CANCELED)
	    @Column(length = 20)
	    private String status;

	    // 결제 수단
	    @Column(length = 30)
	    private String method;

	    private Integer amount;

	    private LocalDateTime approvedAt;

	    @Column(updatable = false)
	    @CreationTimestamp
	    private LocalDateTime createdAt;
}
