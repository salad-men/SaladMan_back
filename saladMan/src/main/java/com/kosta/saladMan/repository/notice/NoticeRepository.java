// NoticeRepository.java
package com.kosta.saladMan.repository.notice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.entity.notice.Notice;
@Repository
public interface NoticeRepository extends JpaRepository<Notice, Integer> {
    Page<Notice> findByTitleContaining(String keyword, Pageable pageable);

}
