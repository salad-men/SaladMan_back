package com.kosta.saladMan.service.notice;

import com.kosta.saladMan.dto.notice.NoticeDto;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

public interface NoticeService {
    Integer writeNotice(NoticeDto noticeDto, MultipartFile img, MultipartFile file) throws Exception;
    void modifyNotice(NoticeDto noticeDto, MultipartFile img, MultipartFile file) throws Exception;
    NoticeDto detailNotice(Integer id) throws Exception;
    Map<String, Object> searchNoticeList(int page, int size, String field, String keyword);
    void deleteNotice(Integer id) throws Exception;
    void deleteImg(String url);
    void deleteFile(String url);
    
}
