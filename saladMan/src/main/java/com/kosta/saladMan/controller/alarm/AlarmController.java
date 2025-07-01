package com.kosta.saladMan.controller.alarm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.saladMan.auth.PrincipalDetails;
import com.kosta.saladMan.dto.alarm.AlarmDto;
import com.kosta.saladMan.entity.store.Store;
import com.kosta.saladMan.service.alarm.AlarmService;
import com.kosta.saladMan.service.alarm.FcmMessageService;
import com.kosta.saladMan.util.PageInfo;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/store")
public class AlarmController {
	
	public final AlarmService alarmService;
	private final FcmMessageService fcmMessageService;
	
	@GetMapping("/notification")
	public ResponseEntity<Map<String, Object>> getStoreAlarm(@AuthenticationPrincipal PrincipalDetails principalDetails,
			@RequestParam String page) throws Exception{
		Store store = principalDetails.getStore();
	    Integer storeId = store.getId();
	    
	    PageInfo pageInfo = new PageInfo(1);
	    if(page != null) {
	    	pageInfo.setCurPage(Integer.parseInt(page));
	    }
	    
	    try {
	    	List<AlarmDto> alarmList = alarmService.getStoreAlarm(storeId, pageInfo);
	    	Map<String, Object> response = new HashMap<>();
	        response.put("data", alarmList);
	        response.put("pageInfo", pageInfo);
	    	return new ResponseEntity<>(response, HttpStatus.OK);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }
	}
	
	@GetMapping("/notification/{id}")
	public ResponseEntity<Boolean> confirmAlarm(@PathVariable Integer id) {
		Boolean confirm = fcmMessageService.confirmAlarm(id);
		return new ResponseEntity<Boolean>(confirm, HttpStatus.OK);
	}
	
	@DeleteMapping("/notification/{id}")
	public ResponseEntity<Boolean> deleteAlarm(@PathVariable Integer id) {
		try {
			boolean deleted = alarmService.deleteAlarm(id);  // 서비스에서 삭제 처리
			return new ResponseEntity<>(deleted, HttpStatus.OK);
		} catch (Exception e) {
	        e.printStackTrace();
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }
	    
	}
}
