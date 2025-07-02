package com.kosta.saladMan.dto.kiosk;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentPrepareResponseDto {
    private String orderId; // ex) ORDER-1234
    private Integer amount;
}
