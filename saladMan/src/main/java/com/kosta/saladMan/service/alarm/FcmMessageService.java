package com.kosta.saladMan.service.alarm;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.kosta.saladMan.dto.alarm.AlarmDto;
import com.kosta.saladMan.entity.alarm.Alarm;
import com.kosta.saladMan.entity.store.Store;
import com.kosta.saladMan.repository.StoreRepository;
import com.kosta.saladMan.repository.alarm.AlarmRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FcmMessageService {
	private final FirebaseMessaging firebaseMessaging;
	private final StoreRepository storeRepository;
	private final AlarmRepository alarmRepository;
	
	public Boolean sendAlarm(AlarmDto messageDto) {
		//1. Id로 fcmToken 가져오기
		Optional<Store> ouser = storeRepository.findById(messageDto.getStoreId());
		if(ouser.isEmpty()) {
			System.out.println("StoreId 오류");
			return false;
		}
		
		String fcmToken = ouser.get().getFcmToken();
		if(fcmToken==null || fcmToken.trim().length()==0) {
			System.out.println("FCM Token 오류");
			return false;
		}
		
		//2. AlarmTable에 저장
		Optional<Store> optionalStore = storeRepository.findById(messageDto.getStoreId());
		if (optionalStore.isEmpty()) {
		    throw new RuntimeException("해당 store가 존재하지 않습니다.");
		}
		Store store = optionalStore.get();

		Alarm alarm = Alarm.builder()
			    .store(store)
			    .title(messageDto.getTitle())
			    .content(messageDto.getContent())
			    .isRead(false)
			    .sentAt(LocalDate.now())
			    .build();
			alarmRepository.save(alarm);
		
		//3. FCM 메시지 전송
//		Notification notification = Notification.builder()
//				.setTitle(alarm.getNum()+"_"+messageDto.getTitle())
//				.setBody(messageDto.getBody())
//				.build();
		
		Message message = Message.builder()
				.setToken(fcmToken)
//				.setNotification(notification)
				.putData("num", alarm.getId()+"")
				.putData("title", messageDto.getTitle())
				.putData("content", messageDto.getContent())
				.build();
		
		try {
			firebaseMessaging.send(message);
			return true;
		} catch(FirebaseMessagingException e) {
			e.printStackTrace();
			return false;
		}
	}

	//알람목록 조회(미확인것만)
	public List<AlarmDto> getAlarmList(Integer storeId) {
		List<Alarm> alarmList = alarmRepository.findByStore_IdAndIsReadFalse(storeId);
		return alarmList.stream().map(alarm->AlarmDto.builder()
									.id(alarm.getId())
									.storeId(alarm.getStore().getId())
									.title(alarm.getTitle())
									.content(alarm.getContent())
									.build())
								  .collect(Collectors.toList());
	}
	//특정알람 확인(알람번호)
	public Boolean confirmAlarm(Integer alarmId) {
		Optional<Alarm> oalarm = alarmRepository.findById(alarmId);
		System.out.println(oalarm.get());
		if(oalarm.isEmpty()) {
			System.out.println("알람번호 오류");
			return false;
		}
		Alarm alarm = oalarm.get();
		alarm.setIsRead(true);
		alarmRepository.save(alarm);
		return true;
	}
	
	//알람목록 확인(알람번호)
	public Boolean confirmAlarmAll(List<Integer> alarmList) {
		for(Integer num : alarmList) {
			Optional<Alarm> oalarm = alarmRepository.findById(num);
			if(oalarm.isPresent()) {
				Alarm alarm = oalarm.get(); 
				alarm.setIsRead(true);
				alarmRepository.save(alarm);
			}
		}
		return true;
	}

	//프론트에서 받은 fcmToken DB에 저장
	public void registFcmToken(Integer storeId, String fcmToken) {
		Optional<Store> ouser = storeRepository.findById(storeId);
		if(ouser.isEmpty()) {
			System.out.println("사용자오류");
			return;
		}
		
		ouser.get().setFcmToken(fcmToken);
		storeRepository.save(ouser.get());
	}
}