package com.kosta.saladMan.repository.user;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.dto.menu.BestMenuDto;
import com.kosta.saladMan.entity.menu.TotalMenu;

@Repository
public interface SalesBestMenuRepository extends CrudRepository<TotalMenu, Integer> {

    @Query(value = "SELECT m.id, m.name, m.img, SUM(oi.quantity) AS totalQuantity " +
                   "FROM sale_order_item oi " +
                   "JOIN total_menu m ON oi.menu_id = m.id " +
                   "GROUP BY m.id, m.name, m.img " +
                   "ORDER BY totalQuantity DESC",
           nativeQuery = true)
    List<Object[]> findBestMenusNative();
}