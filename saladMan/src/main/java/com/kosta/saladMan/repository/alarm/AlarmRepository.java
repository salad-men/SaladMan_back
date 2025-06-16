package com.kosta.saladMan.repository.alarm;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kosta.saladMan.entity.alarm.Alarm;

public interface AlarmRepository extends JpaRepository<Alarm, Integer> {
}
