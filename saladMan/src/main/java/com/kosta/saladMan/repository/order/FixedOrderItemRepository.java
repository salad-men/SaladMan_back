package com.kosta.saladMan.repository.order;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.entity.purchaseOrder.FixedOrderItem;

@Repository
public interface FixedOrderItemRepository extends JpaRepository<FixedOrderItem, Integer> {
	
	@Modifying
	@Transactional
	@Query("delete from FixedOrderItem f where f.fixedOrderTemplate.id = :templateId")
	int deleteByFixedOrderTemplateId(@Param("templateId") Integer templateId);

    List<FixedOrderItem> findByFixedOrderTemplateIdAndAutoOrderEnabledTrue(Integer templateId);

}
