package com.kosta.saladMan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.saladMan.controller.common.S3Uploader;

@Service
public class TestServiceImpl {
    @Autowired
    private S3Uploader s3Uploader;

    public String uploadFile(MultipartFile file) throws Exception {
        // "test" 폴더 밑에 저장
        return s3Uploader.upload(file, "test");
    }
}
