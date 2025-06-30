package com.kosta.saladMan.controller.store.notice;

import com.kosta.saladMan.dto.notice.NoticeDto;
import com.kosta.saladMan.service.notice.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/store/notice")
public class StoreNoticeController {

    private final NoticeService noticeService;

    // 공지 목록 (검색, 페이징)
    @PostMapping("/list")
    public ResponseEntity<Map<String, Object>> list(@RequestBody(required = false) Map<String, String> param) {
        int page = 0;
        int size = 10;
        String field = "title";
        String keyword = "";

        if (param != null) {
            if (param.get("page") != null) page = Integer.parseInt(param.get("page"));
            if (param.get("size") != null) size = Integer.parseInt(param.get("size"));
            if (param.get("field") != null) field = param.get("field");
            if (param.get("keyword") != null) keyword = param.get("keyword");
        }

        Map<String, Object> res = new HashMap<>();
        try {
            Map<String, Object> data = noticeService.searchNoticeList(page, size, field, keyword);
            res.putAll(data);
            res.put("result", "ok");
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            res.put("result", "fail");
            res.put("msg", e.getMessage());
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
    }

    // 공지 상세
    @GetMapping("/detail")
    public ResponseEntity<Map<String, Object>> detail(@RequestParam("id") Integer id) {
        Map<String, Object> res = new HashMap<>();
        try {
            NoticeDto dto = noticeService.detailNotice(id);
            res.put("notice", dto);
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
