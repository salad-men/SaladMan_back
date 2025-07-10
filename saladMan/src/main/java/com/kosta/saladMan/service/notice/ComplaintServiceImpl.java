package com.kosta.saladMan.service.notice;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import com.kosta.saladMan.dto.alarm.AlarmDto;
import com.kosta.saladMan.dto.alarm.SendAlarmDto;
import com.kosta.saladMan.dto.notice.ComplaintDto;
import com.kosta.saladMan.entity.alarm.AlarmMsg;
import com.kosta.saladMan.entity.notice.Complaint;
import com.kosta.saladMan.repository.notice.ComplaintRepository;
import com.kosta.saladMan.service.alarm.FcmMessageService;
import com.kosta.saladMan.util.PageInfo;
import com.kosta.saladMan.repository.alarm.AlarmMsgRepository;
import com.kosta.saladMan.repository.notice.ComplaintDslRepository;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintDslRepository complaintDslRepository; 
    private final ComplaintRepository complaintRepository;
    private final FcmMessageService fcmMessageService;
    private final AlarmMsgRepository alarmMsgRepository;

    @Override
    public List<ComplaintDto> searchHqComplaintList(PageInfo pageInfo, String keyword) {
        int curPage = pageInfo.getCurPage() == null || pageInfo.getCurPage() < 1 ? 1 : pageInfo.getCurPage();
        PageRequest pageRequest = PageRequest.of(curPage - 1, 10);
        Boolean isRelay = false;
        List<Complaint> complaintList = complaintDslRepository.findComplaintsByFilters(pageRequest, null, null, null, isRelay, keyword);
        Long totalCount = complaintDslRepository.countComplaintsByFilters(null, null, null, isRelay, keyword);

        int allPage = (int) Math.ceil((double) totalCount / pageRequest.getPageSize());
        pageInfo.setAllPage(allPage);
        int startPage = (pageInfo.getAllPage() - 1) / 10 * 10 + 1;
        pageInfo.setStartPage(startPage);
        int endPage = Math.min(startPage + 10 - 1, pageInfo.getAllPage());
        pageInfo.setEndPage(endPage);

        return complaintList.stream().map(Complaint::toDto).collect(Collectors.toList());
    }

    // Store: 매장 목록 (isRelay=true + storeId)
    @Override
    public List<ComplaintDto> searchStoreComplaintList(PageInfo pageInfo, Integer storeId, String keyword) {
        int curPage = pageInfo.getCurPage() == null || pageInfo.getCurPage() < 1 ? 1 : pageInfo.getCurPage();
        PageRequest pageRequest = PageRequest.of(curPage - 1, 10);
        Boolean isRelay = true;
        List<Complaint> complaintList = complaintDslRepository.findComplaintsByFilters(pageRequest, storeId, null, null, isRelay, keyword);
        Long totalCount = complaintDslRepository.countComplaintsByFilters(storeId, null, null, isRelay, keyword);

        int allPage = (int) Math.ceil((double) totalCount / pageRequest.getPageSize());
        pageInfo.setAllPage(allPage);
        int startPage = (pageInfo.getAllPage() - 1) / 10 * 10 + 1;
        pageInfo.setStartPage(startPage);
        int endPage = Math.min(startPage + 10 - 1, pageInfo.getAllPage());
        pageInfo.setEndPage(endPage);

        return complaintList.stream().map(Complaint::toDto).collect(Collectors.toList());
    }

    // HQ 상세 (isHqRead 처리)
    @Override
    @Transactional
    public ComplaintDto detailComplaintHq(Integer id) throws Exception {
        Complaint entity = complaintRepository.findById(id)
                .orElseThrow(() -> new Exception("불편사항 없음"));
        if (!Boolean.TRUE.equals(entity.getIsHqRead())) {
            entity.setIsHqRead(true);
            complaintRepository.save(entity);
        }
        return entity.toDto();
    }

    // Store 상세 (isStoreRead 처리)
    @Override
    @Transactional
    public ComplaintDto detailComplaintStore(Integer id) throws Exception {
        Complaint entity = complaintRepository.findById(id)
                .orElseThrow(() -> new Exception("불편사항 없음"));
        if (!Boolean.TRUE.equals(entity.getIsStoreRead())) {
            entity.setIsStoreRead(true);
            complaintRepository.save(entity);
        }
        return entity.toDto();
    }

    @Override
    @Transactional
    public void forwardComplaint(Integer id) throws Exception {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new Exception("불편사항 없음"));
        
        if (!complaint.getIsRelay()) {
	        // 본사가 매장에게 전달: is_relay = 1 으로 변경
	        complaint.setIsRelay(true);
	        complaint.setIsHqRead(true);   // 본사가 읽음 표시
	        complaintRepository.save(complaint);
	        
	        SendAlarmDto alarmDto = SendAlarmDto.builder()
	                .storeId(complaint.getStore().getId())
	                .alarmMsgId(1) // 템플릿 ID만 넘김
	                .build();

	        fcmMessageService.sendAlarm(alarmDto);
	        System.out.println("Complaint-Alarm :"+alarmDto);
        }
    }
    
    @Override
    public ComplaintDto save(ComplaintDto dto) {
        return complaintRepository.save(dto.toEntity()).toDto();
    }
}
