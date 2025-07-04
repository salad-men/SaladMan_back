package com.kosta.saladMan.controller.common;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * S3Uploader 유틸
 * <p>
 * - 단일 파일 업로드, 멀티 파일 업로드, 파일 삭제 지원
 * - CloudFront URL 반환/파싱 지원
 * - 로컬(access-key), EC2(IAM Role) 모두 자동 지원
 *
 * 사용법:
 *   - s3Uploader.upload(file, "images")
 *   - s3Uploader.uploadMulti(files, "temp")
 *   - s3Uploader.delete("images/uuid.png")
 *   - s3Uploader.extractKeyFromUrl("https://CLOUDFRONT/images/uuid.png")
 */
@Component
@RequiredArgsConstructor
public class S3Uploader {

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${cloudfront.base-url}")
    private String cloudFrontUrl;

    @Value("${aws.s3.access-key:}")
    private String accessKey;

    @Value("${aws.s3.secret-key:}")
    private String secretKey;

    /**
     * 단일 파일 업로드
     *
     * @param file    업로드할 파일 (MultipartFile)
     * @param dirName S3 폴더명 (ex. "images", "profile")
     * @return CloudFront URL
     * @throws Exception
     */
    public String upload(MultipartFile file, String dirName) throws Exception {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("빈 파일입니다!");

        String uuid = UUID.randomUUID().toString();
        String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'));
        String key = dirName + "/" + uuid + ext;

        S3Client s3 = getS3Client();

        try (InputStream is = file.getInputStream()) {
            PutObjectRequest req = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();
            s3.putObject(req, RequestBody.fromInputStream(is, file.getSize()));
        }
        return cloudFrontUrl + "/" + key;
    }

    /**
     * 멀티 파일 업로드
     *
     * @param files   파일 리스트/배열
     * @param dirName S3 폴더명
     * @return CloudFront URL 리스트
     */
    public List<String> uploadMulti(List<MultipartFile> files, String dirName) throws Exception {
        List<String> result = new ArrayList<>();
        if (files == null) return result;
        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                result.add(upload(file, dirName));
            }
        }
        return result;
    }

    /**
     * S3 key로 파일 삭제
     *
     * @param key S3 내의 파일 경로 (ex. "images/uuid.png")
     * @return true 성공, false 실패
     */
    public boolean delete(String key) {
        try {
            S3Client s3 = getS3Client();
            DeleteObjectRequest req = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            s3.deleteObject(req);
            return true;
        } catch (Exception e) {
            // 필요시 로그 추가
            return false;
        }
    }

    /**
     * CloudFront URL에서 S3 key 추출
     *
     * @param url CloudFront URL
     * @return S3 key ("images/uuid.png" 등)
     */
    public String extractKeyFromUrl(String url) {
        if (url == null) return null;
        return url.replace(cloudFrontUrl + "/", "");
    }

    // 내부용: S3Client 환경별 생성 (로컬: access-key, 운영: IAM Role)
    private S3Client getS3Client() {
        if (accessKey != null && !accessKey.isBlank() && secretKey != null && !secretKey.isBlank()) {
            return S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(
                            StaticCredentialsProvider.create(
                                    AwsBasicCredentials.create(accessKey, secretKey)
                            )
                    )
                    .build();
        } else {
            return S3Client.builder()
                    .region(Region.of(region))
                    .build();
        }
    }
    
    /**
     * File 파일 업로드
     *
     * @param file 업로드할 File 객체
     * @param dirName S3 폴더명
     * @return CloudFront URL
     * @throws Exception
     */
    public String uploadInputStream(File file, String dirName) throws Exception {
        if (file == null || !file.exists()) throw new IllegalArgumentException("File이 존재하지 않습니다!");
        if (file.getName() == null || !file.getName().contains(".")) throw new IllegalArgumentException("파일명이 유효하지 않습니다.");

        String uuid = UUID.randomUUID().toString();
        String ext = file.getName().substring(file.getName().lastIndexOf('.'));
        String key = dirName + "/" + uuid + ext;

        // contentType 유추 (간단히 PNG 고정하거나 필요시 로직 추가)
        String contentType = "image/png";

        try (InputStream is = new FileInputStream(file)) {
            S3Client s3 = getS3Client();

            PutObjectRequest req = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(contentType)
                    .build();

            s3.putObject(req, RequestBody.fromInputStream(is, file.length()));
        }

        return cloudFrontUrl + "/" + key;
    }
}
