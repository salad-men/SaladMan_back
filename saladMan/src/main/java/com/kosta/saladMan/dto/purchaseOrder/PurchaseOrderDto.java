package com.kosta.saladMan.dto.purchaseOrder;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.kosta.saladMan.entity.purchaseOrder.PurchaseOrder;
import com.kosta.saladMan.entity.store.Store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PurchaseOrderDto {
	private Integer id;
	private Integer storeId;
	private LocalDateTime orderDateTime;
	private String status;
	private String orderStatus;
	private String requestedBy;
	private Integer totalPrice;
	private String purType; // 발주유형
	private String qrImg;

	private String productNameSummary; // 이름 요약 ex.양상추 외 3
	private String quantitySummary; // 수량 요약 5/7
	private Boolean receiptAvailable; // 입고 상태면 true 발주서용
	private String storeName;

	public PurchaseOrderDto(Integer id, Integer storeId, LocalDateTime orderDateTime, String status, String requestedBy,
			Integer totalPrice, String purType, String qrImg, String orderStatus, String productNameSummary, String quantitySummary,
			Boolean receiptAvailable, String storeName) {
		this.id = id;
		this.storeId = storeId;
		this.orderDateTime = orderDateTime;
		this.status = status;
		this.requestedBy = requestedBy;
		this.totalPrice = totalPrice;
		this.purType = purType;
		this.qrImg = qrImg;
		this.orderStatus=orderStatus;
		this.productNameSummary = productNameSummary;
		this.quantitySummary = quantitySummary;
		this.receiptAvailable = receiptAvailable;
		this.storeName = storeName;
	}

	public PurchaseOrder toEntity() {
		return PurchaseOrder.builder().id(id).store(Store.builder().id(storeId).build()).orderDateTime(orderDateTime)
				.status(status).orderStatus(orderStatus).requestedBy(requestedBy).totalPrice(totalPrice)
				.purType(purType).qrImg(qrImg).build();
	}
}
