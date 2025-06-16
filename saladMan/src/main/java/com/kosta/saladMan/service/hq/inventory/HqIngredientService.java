package com.kosta.saladMan.service.hq.inventory;

import java.util.List;

import com.kosta.saladMan.dto.inventory.HqIngredientDto;
import com.kosta.saladMan.dto.inventory.IngredientDto;
import com.kosta.saladMan.util.PageInfo;

public interface HqIngredientService {
	//목록 조회
    List<HqIngredientDto> searchHqInventory(PageInfo pageInfo, String category, String name);
    //추가
    void addHqIngredient(HqIngredientDto dto);
    //수정
    void updateHqIngredient(HqIngredientDto dto);
    HqIngredientDto getById(Integer id);
}
