package com.kosta.saladMan.service.notice;

import java.util.List;
import java.util.Map;

import com.kosta.saladMan.dto.notice.ComplaintDto;
import com.kosta.saladMan.util.PageInfo;

public interface ComplaintService {
    ComplaintDto save(ComplaintDto dto);
    List<ComplaintDto> searchHqComplaintList(PageInfo pageInfo, String keyword);
    List<ComplaintDto> searchStoreComplaintList(PageInfo pageInfo, Integer storeId, String keyword);
    ComplaintDto detailComplaintHq(Integer id) throws Exception;
    ComplaintDto detailComplaintStore(Integer id) throws Exception;
    void forwardComplaint(Integer id) throws Exception;
}
