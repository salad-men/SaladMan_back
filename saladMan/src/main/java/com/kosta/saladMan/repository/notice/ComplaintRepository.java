// ComplaintRepository.java
package com.kosta.saladMan.repository.notice;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kosta.saladMan.entity.notice.Complaint;

public interface ComplaintRepository extends JpaRepository<Complaint, Integer> {
}
