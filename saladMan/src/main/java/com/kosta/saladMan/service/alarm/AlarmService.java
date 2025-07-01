package com.kosta.saladMan.service.alarm;

import java.util.List;

import com.kosta.saladMan.dto.alarm.AlarmDto;
import com.kosta.saladMan.util.PageInfo;

public interface AlarmService {
	public void sendAlarmToStore(Integer storeId, String type, String title) throws Exception;
	List<AlarmDto> getStoreAlarm(Integer storeId, PageInfo pageInfo) throws Exception;
	boolean deleteAlarm(Integer id) throws Exception;
	
}
