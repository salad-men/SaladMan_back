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
}
