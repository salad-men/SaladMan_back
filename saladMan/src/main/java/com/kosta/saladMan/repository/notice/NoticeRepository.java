// NoticeRepository.java
package com.kosta.saladMan.repository.notice;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kosta.saladMan.entity.notice.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Integer> {
}
