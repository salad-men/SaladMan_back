package com.kosta.saladMan.repository.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.entity.inventory.Disposal;
@Repository
public interface DisposalRepository extends JpaRepository<Disposal, Integer> {
}
