package com.kosta.saladMan.service.store;

import java.util.List;

import com.kosta.saladMan.dto.store.ScheduleDto;

public interface ScheduleService {
    void saveSchedules(List<ScheduleDto> dtos); 
    List<ScheduleDto> getSchedulesByStoreAndMonth(Integer storeId, int year, int month);
}
