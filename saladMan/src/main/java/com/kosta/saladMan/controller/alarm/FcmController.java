package com.kosta.saladMan.controller.alarm;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.saladMan.dto.alarm.AlarmDto;
import com.kosta.saladMan.dto.alarm.AlarmMsgDto;
import com.kosta.saladMan.service.alarm.FcmMessageService;

// 발신자가 필요하며, recvId가 username인지 storeid가 될지
// 토큰은 username으로 가져오고 잇음
// 근데 알람이 그냥 시스템?상으로 뭐가 도착햇다고만 알려주는거라 recvId가 잇는것도 애매쓰 
@RestController
public class FcmController {
	
	@Autowired
	private FcmMessageService fcmMessageService;
	
	@PostMapping("/fcmToken")
	public ResponseEntity<String> fcmToken(@RequestBody Map<String,String> param) {
		System.out.println(param);
		Integer storeId = Integer.parseInt(param.get("storeId"));
		fcmMessageService.registFcmToken(storeId, param.get("fcmToken"));
		return new ResponseEntity<String>("true", HttpStatus.OK);
	}
	
	@PostMapping("/alarms")
	public ResponseEntity<List<AlarmDto>> alarms(@RequestBody Map<String,String> param) {
		Integer storeId = Integer.parseInt(param.get("storeId"));
		List<AlarmDto> alarms = fcmMessageService.getAlarmList(storeId);
		return new ResponseEntity<>(alarms, HttpStatus.OK);
	}
	
	@GetMapping("/confirm/{num}")
	public ResponseEntity<Boolean> confirmAlarm(@PathVariable Integer num) {
		Boolean confirm = fcmMessageService.confirmAlarm(num);
		return new ResponseEntity<Boolean>(confirm, HttpStatus.OK);
	}
	
	@PostMapping("/confirmAll")
	public ResponseEntity<Boolean> confirmAlarmAll(@RequestBody Map<String, List<Integer>> param) {
		System.out.println(param);
		Boolean confirm = fcmMessageService.confirmAlarmAll(param.get("alarmList"));
		return new ResponseEntity<Boolean>(confirm, HttpStatus.OK);
	}
	
	@PostMapping("/sendAlarm")
	public ResponseEntity<Boolean> sendAlarm(@RequestBody AlarmDto messageDto) {
		System.out.println(messageDto);
		Boolean sendSucces = fcmMessageService.sendAlarm(messageDto);
		return new ResponseEntity<Boolean>(sendSucces, HttpStatus.OK);
	}
}
