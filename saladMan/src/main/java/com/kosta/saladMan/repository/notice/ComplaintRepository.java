// ComplaintRepository.java
package com.kosta.saladMan.repository.notice;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.entity.notice.Complaint;
@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Integer> {
    List<Complaint> findByStoreIdAndIsRelayTrue(Integer storeId);
    
}
