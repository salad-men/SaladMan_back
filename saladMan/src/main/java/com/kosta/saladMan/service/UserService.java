package com.kosta.saladMan.service;

import org.springframework.stereotype.Service;

import com.kosta.saladMan.dto.UserDto;
import com.kosta.saladMan.entity.User;
import com.kosta.saladMan.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public List<UserDto> getUsers() {
        return repo.findAll()
                   .stream()
                   .map(user -> new UserDto(user.getId(), user.getName()))
                   .collect(Collectors.toList());
    }

    public UserDto createUser(UserDto dto) {
        User user = new User(dto.getId(), dto.getName());
        User saved = repo.save(user);
        return new UserDto(saved.getId(), saved.getName());
    }
}
