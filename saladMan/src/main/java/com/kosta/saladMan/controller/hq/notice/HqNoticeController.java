package com.kosta.saladMan.controller.hq.notice;

import com.kosta.saladMan.dto.notice.NoticeDto;
import com.kosta.saladMan.service.notice.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hq/notice")
public class HqNoticeController {

    private final NoticeService noticeService;

    @PostMapping("/write")
    public ResponseEntity<Map<String, Object>> write(
            @ModelAttribute NoticeDto noticeDto,
            @RequestParam(value = "img", required = false) MultipartFile img,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        Map<String, Object> res = new HashMap<>();
        try {
            Integer id = noticeService.writeNotice(noticeDto, img, file);
            NoticeDto saved = noticeService.detailNotice(id);
            res.put("notice", saved);
            res.put("result", "ok");
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            res.put("result", "fail");
            res.put("msg", e.getMessage());
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/modify")
    public ResponseEntity<Map<String, Object>> modify(
            @ModelAttribute NoticeDto noticeDto,
            @RequestParam(value = "img", required = false) MultipartFile img,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        Map<String, Object> res = new HashMap<>();
        try {
            noticeService.modifyNotice(noticeDto, img, file);
            NoticeDto updated = noticeService.detailNotice(noticeDto.getId());
            res.put("notice", updated);
            res.put("result", "ok");
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            res.put("result", "fail");
            res.put("msg", e.getMessage());
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
    }

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
    
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteNotice(@RequestParam("id") Integer id) {
        Map<String, Object> res = new HashMap<>();
        try {
            noticeService.deleteNotice(id);
            res.put("result", "ok");
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            res.put("result", "fail");
            res.put("msg", e.getMessage());
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/img")
    public ResponseEntity<Map<String, Object>> deleteImg(@RequestParam("url") String url) {
        Map<String, Object> res = new HashMap<>();
        try {
            noticeService.deleteImg(url);
            res.put("result", "ok");
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            res.put("result", "fail");
            res.put("msg", e.getMessage());
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/file")
    public ResponseEntity<Map<String, Object>> deleteFile(@RequestParam("url") String url) {
        Map<String, Object> res = new HashMap<>();
        try {
            noticeService.deleteFile(url);
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
