//package com.kosta.saladMan.service.hq.inventory;
//
//
//import com.kosta.saladMan.dto.inventory.HqIngredientDto;
//import com.kosta.saladMan.entity.inventory.HqIngredient;
//import com.kosta.saladMan.repository.inventory.HqIngredientRepository;
//import com.kosta.saladMan.util.PageInfo;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class HqIngredientServiceImpl implements HqIngredientService {
//    private final HqInventoryDslRepository dsl;
//    private final HqIngredientRepository repo;
//
//    @Override
//    public List<HqIngredientDto> searchHqInventory(PageInfo pageInfo, String category, String name) {
//        return dsl.searchHq(pageInfo, category, name);
//    }
//
//    @Override
//    public HqIngredientDto getById(Integer id) {
//        return repo.findById(id)
//                   .map(HqIngredient::toDto)
//                   .orElseThrow(() -> new RuntimeException("HQ Ingredient not found: " + id));
//    }
//
//    @Override
//    public void addHqIngredient(HqIngredientDto dto) {
//        HqIngredient entity = dto.toEntity();
//        repo.save(entity);
//    }
//
//    @Override
//    public void updateHqIngredient(HqIngredientDto dto) {
//        HqIngredient entity = repo.findById(dto.getId())\            .orElseThrow(() -> new RuntimeException("HQ Ingredient not found: " + dto.getId()));
//        entity.setExpiredQuantity(dto.getExpiredQuantity());
//        entity.setMinimumOrderUnit(dto.getMinimumOrderUnit());
//        entity.setUnitCost(dto.getUnitCost());
//        entity.setQuantity(dto.getQuantity());
//        repo.save(entity);
//    }
//}