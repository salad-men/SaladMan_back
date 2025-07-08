package com.kosta.saladMan.service.notice;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import com.kosta.saladMan.dto.alarm.AlarmDto;
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
    public List<ComplaintDto> searchComplaintList(PageInfo pageInfo, Integer storeId, String status, String keyword) {
        int curPage = pageInfo.getCurPage() == null || pageInfo.getCurPage() < 1 ? 1 : pageInfo.getCurPage();
        PageRequest pageRequest = PageRequest.of(curPage - 1, 10);

        Boolean isRead = null;
        Boolean isRelay = null;

        // storeId가 있으면 '매장 요청' → 전달된(isRelay=1)만 보여주기
        if (storeId != null) {
            isRelay = true;
        } else {
            // 본사 요청 → 전달 안 된(isRelay=0)만 보여주기
            isRelay = false;
        }

        List<Complaint> complaintList = complaintDslRepository.findComplaintsByFilters(pageRequest, storeId, isRead, isRelay, keyword);
        Long totalCount = complaintDslRepository.countComplaintsByFilters(storeId, isRead, isRelay, keyword);

        int allPage = (int) Math.ceil((double) totalCount / pageRequest.getPageSize());
        pageInfo.setAllPage(allPage);

        int startPage = (pageInfo.getAllPage() - 1) / 10 * 10 + 1;
        pageInfo.setStartPage(startPage);

        int endPage = Math.min(startPage + 10 - 1, pageInfo.getAllPage());
        pageInfo.setEndPage(endPage);

        return complaintList.stream()
                .map(c -> {
                    // 매장에선 isRead 항상 false(미열람)로 내려줌
                    ComplaintDto dto = c.toDto();
                    if (storeId != null) dto.setIsRead(false);
                    return dto;
                })
                .collect(Collectors.toList());
    }


    @Override
    public ComplaintDto detailComplaint(Integer id) throws Exception {
        Complaint entity = complaintRepository.findById(id)
                .orElseThrow(() -> new Exception("불편사항 없음"));
        
        // 미열람 상태라면, 열람으로 바꿔주기 (한 번만)
        if (!entity.getIsRead()) {
            entity.setIsRead(true);
            complaintRepository.save(entity);
        }
        return entity.toDto();
    }

    @Override
    @Transactional
    public void forwardComplaint(Integer id) throws Exception {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new Exception("불편사항 없음"));

        complaint.setIsRelay(true);  // 전달 완료
        complaint.setIsRead(true);   // 본사가 읽음 표시
        complaintRepository.save(complaint);
        
        
        AlarmMsg alarmMsg = alarmMsgRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("알림 메시지 없음"));
        
        AlarmDto alarmDto = new AlarmDto();
        alarmDto.setStoreId(complaint.getStore().getId());
        alarmDto.setTitle(alarmMsg.getTitle());
        alarmDto.setContent(alarmMsg.getContent());
        fcmMessageService.sendAlarm(alarmDto);
        System.out.println("Complaint-Alarm :"+alarmDto);
    }
    
    @Override
    public ComplaintDto save(ComplaintDto dto) {
        return complaintRepository.save(dto.toEntity()).toDto();
    }
}
