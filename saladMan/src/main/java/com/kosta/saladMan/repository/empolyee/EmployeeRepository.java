package com.kosta.saladMan.repository.empolyee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.kosta.saladMan.entity.store.Employee;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    // 매장별 직원 전체를 이름 오름차순으로 조회 (근무 중 여부 상관없이)
    List<Employee> findByStoreIdOrderByNameAsc(Integer storeId);
}
