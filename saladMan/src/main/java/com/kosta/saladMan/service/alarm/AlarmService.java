package com.kosta.saladMan.service.alarm;


public interface AlarmService {
	public void sendAlarmToStore(Integer storeId, String type, String title) throws Exception;
}
