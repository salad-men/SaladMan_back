package com.kosta.saladMan.controller.store.notice;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.saladMan.util.PageInfo;

@RestController
@RequestMapping("/store")
public class StoreNotificationController {
	
//	@PostMapping("/notification")
//	public ResponseEntity<Map<String, Object>> storeNoti(@RequestBody(required = false) Map<String, String> param) {
//		PageInfo pageInfo = new PageInfo(1);
//		
//		if(param.get("page")!=null) {
//			pageInfo.setCurPage(Integer.parseInt(param.get("page")));
//		}
//		
//		try {
//			List<alarmDto> alarmList = alarmService.getAlarmList(pageInfo, storeId);
//			Map<String, Object> res = new HashMap<>();
//			res.put("pageInfo", pageInfo);
//			res.put("storeId", storeId);
//			return new ResponseEntity<>(res, HttpStatus.OK);
//		} catch (Exception e) {
//			e.printStackTrace();
//	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//		}
//	}	
	
}
