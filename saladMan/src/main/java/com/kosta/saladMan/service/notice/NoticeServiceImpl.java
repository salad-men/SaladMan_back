package com.kosta.saladMan.service.notice;

import com.kosta.saladMan.controller.common.S3Uploader;
import com.kosta.saladMan.dto.alarm.AlarmDto;
import com.kosta.saladMan.dto.notice.NoticeDto;
import com.kosta.saladMan.entity.alarm.AlarmMsg;
import com.kosta.saladMan.entity.notice.Notice;
import com.kosta.saladMan.entity.store.Store;
import com.kosta.saladMan.repository.StoreRepository;
import com.kosta.saladMan.repository.alarm.AlarmMsgRepository;
import com.kosta.saladMan.repository.notice.NoticeRepository;
import com.kosta.saladMan.service.alarm.FcmMessageService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final S3Uploader s3Uploader;
    //fcm알람
    private final AlarmMsgRepository alarmMsgRepository;
    private final FcmMessageService fcmMessageService;
    private final StoreRepository storeRepository;

    @Override
    @Transactional
    public Integer writeNotice(NoticeDto noticeDto, MultipartFile img, MultipartFile file) throws Exception {
        if (img != null && !img.isEmpty()) {
            String imgUrl = s3Uploader.upload(img, "notice-img");
            noticeDto.setImgFileName(imgUrl);
        }
        if (file != null && !file.isEmpty()) {
            String fileUrl = s3Uploader.upload(file, "notice-file");
            noticeDto.setFileName(fileUrl); 
            noticeDto.setFileOriginName(file.getOriginalFilename()); // 원본 파일명 저장
        }
        Notice entity = noticeDto.toEntity();
        noticeRepository.save(entity);
        
        //alarm
        AlarmMsg alarmMsg = alarmMsgRepository.findById(4)
                .orElseThrow(() -> new RuntimeException("알림 메시지 없음"));
        
        List<Store> storeList = storeRepository.findAll();
        
        for (Store store : storeList) {
            AlarmDto alarmDto = new AlarmDto();
            alarmDto.setStoreId(store.getId());
            alarmDto.setTitle(alarmMsg.getTitle());
            alarmDto.setContent(alarmMsg.getContent());
            
            fcmMessageService.sendAlarm(alarmDto);
        }
        //
        return entity.getId();
    }

    @Override
    @Transactional
    public void modifyNotice(NoticeDto noticeDto, MultipartFile img, MultipartFile file) throws Exception {
        Notice entity = noticeRepository.findById(noticeDto.getId())
                .orElseThrow(() -> new Exception("공지 없음"));
        entity.setTitle(noticeDto.getTitle());
        entity.setContent(noticeDto.getContent());

        // 이미지 교체(삭제 후 업로드)
        if (img != null && !img.isEmpty()) {
            if (entity.getImgFileName() != null && !entity.getImgFileName().isBlank()) {
                String key = s3Uploader.extractKeyFromUrl(entity.getImgFileName());
                s3Uploader.delete(key);
            }
            String imgUrl = s3Uploader.upload(img, "notice-img");
            entity.setImgFileName(imgUrl);
        }

        // 파일 교체(삭제 후 업로드)
        if (file != null && !file.isEmpty()) {
            if (entity.getFileName() != null && !entity.getFileName().isBlank()) {
                String key = s3Uploader.extractKeyFromUrl(entity.getFileName());
                s3Uploader.delete(key);
            }
            String fileUrl = s3Uploader.upload(file, "notice-file");
            entity.setFileName(fileUrl);
            entity.setFileOriginName(file.getOriginalFilename());  
        }

        noticeRepository.save(entity);
    }

    @Override
    public NoticeDto detailNotice(Integer id) throws Exception {
        Notice entity = noticeRepository.findById(id)
                .orElseThrow(() -> new Exception("공지 없음"));
        return entity.toDto();
    }

    @Override
    public Map<String, Object> searchNoticeList(int page, int size, String field, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Notice> pages;
        if (keyword == null || keyword.isBlank()) {
            pages = noticeRepository.findAll(pageable);
        } else if ("title".equals(field)) {
            pages = noticeRepository.findByTitleContaining(keyword, pageable);
        } else {
            pages = noticeRepository.findAll(pageable);
        }
        List<NoticeDto> noticeList = pages.getContent().stream().map(Notice::toDto).collect(Collectors.toList());

        Map<String, Object> map = new HashMap<>();
        map.put("noticeList", noticeList);
        map.put("totalPages", pages.getTotalPages());
        map.put("page", page);
        map.put("size", size);
        return map;
    }
    
    @Override
    @Transactional
    public void deleteNotice(Integer id) throws Exception {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new Exception("공지 없음"));

        // 이미지 파일 삭제
        if (notice.getImgFileName() != null && !notice.getImgFileName().isBlank()) {
            String key = s3Uploader.extractKeyFromUrl(notice.getImgFileName());
            s3Uploader.delete(key);
        }

        // 첨부 파일 삭제
        if (notice.getFileName() != null && !notice.getFileName().isBlank()) {
            String key = s3Uploader.extractKeyFromUrl(notice.getFileName());
            s3Uploader.delete(key);
        }

        noticeRepository.delete(notice);
    }

    @Override
    public void deleteImg(String url) {
        if (url != null && !url.isBlank()) {
            String key = s3Uploader.extractKeyFromUrl(url);
            s3Uploader.delete(key);
        }
    }

    @Override
    public void deleteFile(String url) {
        if (url != null && !url.isBlank()) {
            String key = s3Uploader.extractKeyFromUrl(url);
            s3Uploader.delete(key);
        }
    }
}
