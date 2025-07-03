package com.kosta.saladMan.service.kiosk;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.saladMan.entity.payment.Payment;
import com.kosta.saladMan.entity.saleOrder.SaleOrder;
import com.kosta.saladMan.repository.kiosk.PaymentRepository;
import com.kosta.saladMan.repository.saleOrder.SaleOrderRepository;

@Service
public class OrderCancellationService {
    @Autowired
    private SaleOrderRepository saleOrderRepository;
    @Autowired
    private PaymentRepository paymentRepository;
	@PersistenceContext
	private EntityManager entityManager;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markOrderCanceled(Integer saleOrderId) {
        SaleOrder saleOrder = saleOrderRepository.findById(saleOrderId)
                .orElseThrow(() -> new RuntimeException("SaleOrder not found"));
        Payment payment = paymentRepository.findBySaleOrder(saleOrder)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        saleOrder.setStatus("결제취소");
        payment.setStatus("CANCELED");

        saleOrderRepository.save(saleOrder);
        paymentRepository.save(payment);
        
        // 강제 flush로 바로 DB 반영
        entityManager.flush();
        entityManager.clear();
    }
}
