package com.kosta.saladMan.repository.alarm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.entity.alarm.AlarmMsg;

@Repository
public interface AlarmMsgRepository extends JpaRepository<AlarmMsg, Integer>{

}
