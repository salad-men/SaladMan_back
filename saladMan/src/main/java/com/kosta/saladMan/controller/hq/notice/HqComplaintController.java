package com.kosta.saladMan.controller.hq.notice;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.kosta.saladMan.dto.notice.ComplaintDto;
import com.kosta.saladMan.dto.store.StoreDto;
import com.kosta.saladMan.entity.notice.Complaint;
import com.kosta.saladMan.service.inventory.InventoryService;
import com.kosta.saladMan.service.notice.ComplaintService;
import com.kosta.saladMan.util.PageInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hq/complaint")
public class HqComplaintController {

    private final ComplaintService complaintService;
    private final InventoryService inventoryService;

    @PostMapping("/list")
    public ResponseEntity<Map<String, Object>> list(@RequestBody Map<String, Object> param) {
        int page = param.get("page") != null ? (Integer) param.get("page") : 1;
        String status = (String) param.get("status");
        Integer storeId = param.get("storeId") != null ? (Integer) param.get("storeId") : null;
        String keyword = (String) param.get("keyword");

        PageInfo pageInfo = new PageInfo(page);

        Map<String, Object> res = new HashMap<>();
        try {
            List<ComplaintDto> complaintList = complaintService.searchComplaintList(pageInfo, storeId, status, keyword);

            // 본사 제외 전체 점포 리스트 추가
            List<StoreDto> storeList = inventoryService.getStoresExceptHQ();
            
            res.put("complaintList", complaintList);
            res.put("pageInfo", pageInfo);
            res.put("storeList", storeList); 
            res.put("result", "ok");

            return ResponseEntity.ok(res);
        } catch (Exception e) {
            e.printStackTrace();
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
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            res.put("result", "fail");
            res.put("msg", e.getMessage());
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
    }

    // 전달 처리 (본사 → 매장)
    @PostMapping("/forward")
    public ResponseEntity<Map<String, Object>> forward(@RequestBody Map<String, Integer> param) {
        Map<String, Object> res = new HashMap<>();
        try {
            Integer id = param.get("id");
            complaintService.forwardComplaint(id);
            res.put("result", "ok");
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            res.put("result", "fail");
            res.put("msg", e.getMessage());
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
    }
}
