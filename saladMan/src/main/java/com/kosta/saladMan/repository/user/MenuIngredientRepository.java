package com.kosta.saladMan.repository.user;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kosta.saladMan.entity.menu.MenuIngredient;
import com.kosta.saladMan.entity.menu.TotalMenu;

public interface MenuIngredientRepository extends JpaRepository<MenuIngredient, Integer> {

    // 유저페이지 -> 메뉴 성분 표시 부분
    List<MenuIngredient> findByMenuId(Integer menuId);
    
    //챗봇 양많은 top3
    @Query("SELECT mi.menu.name, SUM(mi.quantity) " +
    	       "FROM MenuIngredient mi " +
    	       "GROUP BY mi.menu.id, mi.menu.name " +
    	       "ORDER BY SUM(mi.quantity) DESC")
    List<Object[]> findTop3SaladsByQuantity(Pageable pageable);
    
    //챗봇 재료 검색 
    @Query("SELECT mi.menu FROM MenuIngredient mi WHERE mi.ingredient.name LIKE %:ingredientName%")
    List<TotalMenu> findMenusByIngredientName(@Param("ingredientName") String ingredientName);
}
	