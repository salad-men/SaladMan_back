// StoreIngredientSettingRepository.java
package com.kosta.saladMan.repository.inventory;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.entity.inventory.StoreIngredientSetting;
@Repository
public interface StoreIngredientSettingRepository extends JpaRepository<StoreIngredientSetting, Integer> {
	List<StoreIngredientSetting> findByStoreId(Integer storeId);
    Optional<StoreIngredientSetting> findByStoreIdAndIngredientId(Integer storeId, Integer ingredientId);

    // 카테고리별 조회
    List<StoreIngredientSetting> findByStore_IdAndIngredient_Category_Id(Integer storeId, Integer categoryId);

    // 매장 전체, 카테고리 전체 등 다양한 조건 가능
    List<StoreIngredientSetting> findByStore_Id(Integer storeId);
	boolean existsByStoreIdAndIngredientId(Integer storeId, Integer ingredientId);
}
