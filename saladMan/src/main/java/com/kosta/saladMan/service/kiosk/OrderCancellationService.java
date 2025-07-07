package com.kosta.saladMan.service.kiosk;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.saladMan.repository.kiosk.PaymentRepository;
import com.kosta.saladMan.repository.saleOrder.SaleOrderRepository;

@Service
public class OrderCancellationService {
    @Autowired
    private SaleOrderRepository saleOrderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markOrderCanceled(Integer saleOrderId) {
        saleOrderRepository.updateStatus(saleOrderId, "결제취소");
        paymentRepository.updateStatus(saleOrderId, "CANCELED");
    }
}
