package com.kosta.saladMan.service.notice;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import com.kosta.saladMan.dto.notice.ComplaintDto;
import com.kosta.saladMan.entity.notice.Complaint;
import com.kosta.saladMan.repository.notice.ComplaintRepository;
import com.kosta.saladMan.util.PageInfo;
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

    @Override
    public List<ComplaintDto> searchComplaintList(PageInfo pageInfo, Integer storeId, String status, String keyword){
        int pageSize = 10;
        int curPage = pageInfo.getCurPage() == null || pageInfo.getCurPage() < 1 ? 1 : pageInfo.getCurPage();

        PageRequest pageRequest = PageRequest.of(curPage - 1, 10);


        Boolean isRead = null;
        Boolean isRelay = null;

        if (status != null) {
            switch (status) {
                case "미열람":
                    isRead = false;
                    isRelay = false;
                    break;
                case "열람":
                    isRead = true;
                    isRelay = false;
                    break;
                case "전달완료":
                    isRelay = true;
                    break;
            }
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
                .map(Complaint::toDto)
                .collect(Collectors.toList());
    }



    @Override
    public ComplaintDto detailComplaint(Integer id) throws Exception {
        Complaint entity = complaintRepository.findById(id)
                .orElseThrow(() -> new Exception("불편사항 없음"));
        return entity.toDto();
    }

    @Override
    @Transactional
    public void forwardComplaint(Integer id) throws Exception {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new Exception("불편사항 없음"));

        // 본사가 매장에게 전달: is_relay = 1 으로 변경
        complaint.setIsRelay(true);
        complaintRepository.save(complaint);
    }
    
    @Override
    public ComplaintDto save(ComplaintDto dto) {
        return complaintRepository.save(dto.toEntity()).toDto();
    }
}
