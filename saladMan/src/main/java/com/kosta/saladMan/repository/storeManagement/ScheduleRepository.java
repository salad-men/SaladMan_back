package com.kosta.saladMan.repository.storeManagement;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.saladMan.entity.store.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    List<Schedule> findByEmployee_Store_IdAndWorkDateBetween(Integer storeId, LocalDate start, LocalDate end);
    void deleteByEmployee_Store_IdAndWorkDateBetween(Integer storeId, LocalDate start, LocalDate end);
}
