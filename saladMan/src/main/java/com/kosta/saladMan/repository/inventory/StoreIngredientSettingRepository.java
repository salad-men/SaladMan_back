// StoreIngredientSettingRepository.java
package com.kosta.saladMan.repository.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kosta.saladMan.entity.inventory.StoreIngredientSetting;

public interface StoreIngredientSettingRepository extends JpaRepository<StoreIngredientSetting, Integer> {
}
