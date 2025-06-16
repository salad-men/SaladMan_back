package com.kosta.saladMan.repository.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kosta.saladMan.entity.inventory.Disposal;

public interface DisposalRepository extends JpaRepository<Disposal, Integer> {
}
