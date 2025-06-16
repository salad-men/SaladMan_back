package com.kosta.saladMan.controller.hq.inventroy;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.saladMan.dto.store.UserDto;
import com.kosta.saladMan.service.UserService;

@RestController
@RequestMapping("/api/users")   
public class HqInventoryController {

    private final UserService service;

    public HqInventoryController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return service.getUsers();
    }

    @PostMapping
    public UserDto createUser(@RequestBody UserDto dto) {
        return service.createUser(dto);
    }
}
