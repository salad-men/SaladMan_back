package com.kosta.saladMan.controller.user;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.saladMan.dto.store.StoreDto;
import com.kosta.saladMan.service.user.StoreService;

@RestController
@RequestMapping("/user/stores")
@CrossOrigin(origins = {
	    "http://localhost:5173", 
	    "http://saladman-web.s3-website.ap-northeast-2.amazonaws.com",
	    "https://www.saladman.net",
	    "http://www.saladman.net"
	}, allowCredentials = "true")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping
    public Page<StoreDto> getStores(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "4") int size) {
        return storeService.getStoresByPage(page, size);
    }
    @GetMapping("/all")
    public List<StoreDto> getAllStores() {
        return storeService.getAllStores();
    }
}
