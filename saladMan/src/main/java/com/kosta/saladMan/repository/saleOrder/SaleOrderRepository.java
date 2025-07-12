package com.kosta.saladMan.repository.saleOrder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.entity.saleOrder.SaleOrder;

@Repository
public interface SaleOrderRepository extends JpaRepository<SaleOrder, Integer> {
	@Modifying
	@Query("UPDATE SaleOrder s SET s.status = :status WHERE s.id = :id")
	void updateStatus(@Param("id") Integer id, @Param("status") String status);
}
