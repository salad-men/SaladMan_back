package com.kosta.saladMan.repository.kiosk;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.entity.menu.MenuCategory;
import com.kosta.saladMan.entity.payment.Payment;
import com.kosta.saladMan.entity.saleOrder.SaleOrder;
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer>{
	Optional<Payment> findBySaleOrder(SaleOrder saleOrder);
	Optional<Payment> findByExternalOrderId(String externalOrderId);

}
