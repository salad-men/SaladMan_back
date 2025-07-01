package com.kosta.saladMan.repository.user;

import com.kosta.saladMan.entity.userBanner.UserBanner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBannerRepository extends JpaRepository<UserBanner, Long> {
}