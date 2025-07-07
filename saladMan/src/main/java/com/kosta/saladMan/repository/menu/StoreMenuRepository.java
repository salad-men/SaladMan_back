package com.kosta.saladMan.repository.menu;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.dto.menu.RecipeDto;
import com.kosta.saladMan.entity.menu.StoreMenu;

@Repository
public interface StoreMenuRepository extends JpaRepository<StoreMenu, Integer> {
	Optional<StoreMenu> findByStoreIdAndMenuId(Integer storeId, Integer menuId);

	@Query("SELECT sm FROM StoreMenu sm WHERE sm.store.id = :storeId AND sm.menu.id IN :menuIds")
	List<StoreMenu> findByStoreAndMenuIds(@Param("storeId") Integer storeId, @Param("menuIds") List<Integer> menuIds);

}
