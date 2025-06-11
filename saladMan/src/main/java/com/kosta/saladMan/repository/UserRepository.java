package com.kosta.saladMan.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.saladMan.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}