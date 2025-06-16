package com.kosta.saladMan.controller.user.store;

import com.kosta.saladMan.dto.store.StoreDto;
import com.kosta.saladMan.service.StoreService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
@CrossOrigin(origins = "*") // React에서 CORS 오류 방지
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping
    public List<StoreDto> getAllStores() {
        return storeService.getAllStores();
    }
}
