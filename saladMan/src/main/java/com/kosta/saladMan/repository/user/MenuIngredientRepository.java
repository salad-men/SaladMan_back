package com.kosta.saladMan.repository.user;

import com.kosta.saladMan.entity.menu.MenuIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MenuIngredientRepository extends JpaRepository<MenuIngredient, Integer> {

    // 유저페이지 -> 메뉴 성분 표시 부분
    List<MenuIngredient> findByMenuId(Integer menuId);
}
	