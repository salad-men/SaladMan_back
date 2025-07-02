package com.kosta.saladMan.repository.user;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.saladMan.entity.notice.Announce;

public interface AnnounceRepository extends JpaRepository<Announce, Integer> {
    Page<Announce> findByTypeOrderByPostedAtDesc(String type, Pageable pageable);
    
}
