package com.kosta.saladMan.service.notice;

import java.util.List;
import java.util.Map;

import com.kosta.saladMan.dto.notice.ComplaintDto;
import com.kosta.saladMan.util.PageInfo;

public interface ComplaintService {
    ComplaintDto save(ComplaintDto dto);
    List<ComplaintDto> searchComplaintList(PageInfo pageInfo, Integer storeId, String status, String keyword);
    ComplaintDto detailComplaint(Integer id) throws Exception;
    void forwardComplaint(Integer id) throws Exception;
}
