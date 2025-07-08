package com.kosta.saladMan.controller.store.notice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.saladMan.dto.notice.ComplaintDto;
import com.kosta.saladMan.service.notice.ComplaintService;
import com.kosta.saladMan.util.PageInfo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/store/complaint")
public class StoreComplaintController {
	
	private final ComplaintService complaintService;
	
	@PostMapping("/list")
	public ResponseEntity<Map<String, Object>> list(@RequestBody Map<String, Object> param) {
	    int page = param.get("page") != null ? (Integer) param.get("page") : 1;
	    Integer storeId = param.get("storeId") != null ? (Integer) param.get("storeId") : null;
	    String keyword = (String) param.get("keyword");

	    PageInfo pageInfo = new PageInfo(page);
	    Map<String, Object> res = new HashMap<>();
	    try {
	        // status 없이 요청해도 됨
	        List<ComplaintDto> complaintList = complaintService.searchComplaintList(pageInfo, storeId, null, keyword);
	        res.put("complaintList", complaintList);
	        res.put("pageInfo", pageInfo);
	        res.put("result", "ok");
	        return ResponseEntity.ok(res);
	    } catch (Exception e) {
	        res.put("result", "fail");
	        res.put("msg", e.getMessage());
	        return ResponseEntity.badRequest().body(res);
	    }
	}

	
    // 상세 조회
    @GetMapping("/detail")
    public ResponseEntity<Map<String, Object>> detail(@RequestParam("id") Integer id) {
        Map<String, Object> res = new HashMap<>();
        try {
            ComplaintDto dto = complaintService.detailComplaint(id);
            res.put("complaint", dto);
            res.put("result", "ok");
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.put("result", "fail");
            res.put("msg", e.getMessage());
            return ResponseEntity.badRequest().body(res);
        }
    }

}
