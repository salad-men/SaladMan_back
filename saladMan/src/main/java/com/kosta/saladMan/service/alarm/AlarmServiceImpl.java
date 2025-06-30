package com.kosta.saladMan.service.alarm;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlarmServiceImpl implements AlarmService {
	
	@Override
	public void sendAlarmToStore(Integer storeId, String type, String title) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
