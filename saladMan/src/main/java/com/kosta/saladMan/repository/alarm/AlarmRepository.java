package com.kosta.saladMan.repository.alarm;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.entity.alarm.Alarm;
import com.kosta.saladMan.entity.store.Store;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Integer> {
	List<Alarm> findByStore_IdAndIsReadFalseOrderBySendAtDesc(Integer storeId);
	Page<Alarm> findByStoreIdOrderBySendAtDesc(Integer storeId, Pageable pageable);
}
