package com.kosta.saladMan.controller.user;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileUploadController {

	@PostMapping("/upload")
	public Map<String, String> upload(@RequestParam("file") MultipartFile file) throws Exception {
	    // 현재 SpringBoot 프로젝트 루트 디렉토리
	    String rootPath = System.getProperty("user.dir");
	    // ../saladMan-frontend/public/ 으로 올라가서 public 폴더에 저장
	    String uploadDir = rootPath + "/../saladMan-frontend/public/";

	    File folder = new File(uploadDir);
	    if (!folder.exists()) folder.mkdirs(); // 폴더 없으면 생성

	    String originalFilename = file.getOriginalFilename();
	    String newFilename = UUID.randomUUID() + "_" + originalFilename;

	    File dest = new File(uploadDir + newFilename);
	    file.transferTo(dest);

	    Map<String, String> result = new HashMap<>();
	    result.put("url", "/" + newFilename);  // React public에 있으니 /로 접근
	    return result;
	}
}
