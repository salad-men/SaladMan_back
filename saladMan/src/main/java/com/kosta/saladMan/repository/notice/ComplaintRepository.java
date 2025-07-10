// ComplaintRepository.java
package com.kosta.saladMan.repository.notice;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.entity.notice.Complaint;
@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Integer> {
    List<Complaint> findByStoreIdAndIsRelayTrue(Integer storeId);
    
    // 매장에서 읽지 않은(isStoreRead=false) + 중계된(isRelay=true) 건수
    int countByStoreIdAndIsRelayTrueAndIsStoreReadFalse(Integer storeId);

    // 본사에서 읽지 않은(isHqRead=false) + 중계 아님(isRelay=false) 건수
    int countByIsRelayFalseAndIsHqReadFalse();
    
}
