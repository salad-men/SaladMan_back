package com.kosta.saladMan.service.alarm;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.kosta.saladMan.dto.alarm.AlarmDto;
import com.kosta.saladMan.entity.alarm.Alarm;
import com.kosta.saladMan.repository.alarm.AlarmRepository;
import com.kosta.saladMan.util.PageInfo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlarmServiceImpl implements AlarmService {
	
	public final AlarmRepository alarmRepository;
	
	@Override
	public void sendAlarmToStore(Integer storeId, String type, String title) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<AlarmDto> getStoreAlarm(Integer storeId, PageInfo pageInfo) throws Exception {
		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 10);
		Page<Alarm> pages = alarmRepository.findByStoreId(storeId, pageRequest);
		
		pageInfo.setAllPage(pages.getTotalPages());
		int startPage = (pageInfo.getAllPage() - 1) / 10 * 10 + 1;
		int endPage = Math.min(startPage + 9, pageInfo.getAllPage());
		pageInfo.setStartPage(startPage);
		pageInfo.setEndPage(endPage);
		
		return pages.getContent().stream()
		        .map(alarm -> new AlarmDto(
		            alarm.getId(),
		            alarm.getStore().getId(),
		            alarm.getIsRead(),
		            alarm.getTitle(),		            
		            alarm.getContent(),
		            alarm.getSentAt()
		        ))
		        .collect(Collectors.toList());
	}
	
	@Override
    public boolean deleteAlarm(Integer id) {
        try {
            if(alarmRepository.existsById(id)) {
                alarmRepository.deleteById(id);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
