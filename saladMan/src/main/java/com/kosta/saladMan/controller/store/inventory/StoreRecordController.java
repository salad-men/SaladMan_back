package com.kosta.saladMan.controller.store.inventory;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.saladMan.dto.inventory.InventoryRecordDto;
import com.kosta.saladMan.service.inventory.InventoryService;
import com.kosta.saladMan.util.PageInfo;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/store/inventory")
@RequiredArgsConstructor
public class StoreRecordController {

    private final InventoryService inventoryService;

    @PostMapping("/record-add")
    public ResponseEntity<Void> add(@RequestBody InventoryRecordDto dto) {
        try {
            if (dto.getIngredientId() == null || dto.getStoreId() == null) {
                return ResponseEntity.badRequest().build();
            }

            inventoryService.addRecord(dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/record")
    public ResponseEntity<Map<String, Object>> getList(
        @RequestParam Integer storeId,
        @RequestParam String type,
        @RequestParam(defaultValue = "1") int page
    ) {
        try {
            PageInfo pageInfo = new PageInfo();
            pageInfo.setCurPage(page);

            List<InventoryRecordDto> list = inventoryService.getRecordsByStoreAndType(storeId, type, pageInfo);

            Map<String, Object> res = new HashMap<>();
            res.put("records", list);
            res.put("pageInfo", pageInfo);  
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }


}
