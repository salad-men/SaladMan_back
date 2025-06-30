package com.kosta.saladMan.repository.order;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.entity.purchaseOrder.FixedOrderTemplate;

@Repository
public interface FixedOrderTemplateRepository extends JpaRepository<FixedOrderTemplate, Integer> {
	Optional<FixedOrderTemplate> findByStoreId(Integer storeId);
}
