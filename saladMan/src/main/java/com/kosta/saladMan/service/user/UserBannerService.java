package com.kosta.saladMan.service.user;

import com.kosta.saladMan.entity.userBanner.UserBanner;

public interface UserBannerService {
    UserBanner getBanner(Long id);
    UserBanner updateBanner(Long id, UserBanner dto);
}