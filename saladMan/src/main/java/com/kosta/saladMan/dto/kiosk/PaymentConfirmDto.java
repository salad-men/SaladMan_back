package com.kosta.saladMan.dto.kiosk;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentConfirmDto {
    private String paymentKey;
    private String orderId;
    private Integer amount;
}
