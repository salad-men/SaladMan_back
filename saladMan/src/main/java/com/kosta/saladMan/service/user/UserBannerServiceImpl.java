package com.kosta.saladMan.service.user;

import com.kosta.saladMan.controller.common.S3Uploader;
import com.kosta.saladMan.entity.userBanner.UserBanner;
import com.kosta.saladMan.repository.user.UserBannerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class UserBannerServiceImpl implements UserBannerService {

    private final UserBannerRepository userBannerRepository;

    @Override
    public UserBanner getBanner(Long id) {
        return userBannerRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("배너가 존재하지 않습니다."));
    }

    @Override
    public UserBanner updateBanner(Long id, UserBanner dto) {
        UserBanner banner = userBannerRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("배너가 존재하지 않습니다."));
        banner.setLine1(dto.getLine1());
        banner.setLine2(dto.getLine2());
        banner.setLine3(dto.getLine3());
        banner.setImage(dto.getImage());
        return userBannerRepository.save(banner);
    }
}

