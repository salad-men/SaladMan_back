package com.kosta.saladMan.repository.empolyee;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kosta.saladMan.entity.store.Employee;
import com.kosta.saladMan.entity.store.QEmployee;
import com.kosta.saladMan.entity.store.QStore;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class EmployeeDslRepository {
	private final JPAQueryFactory queryFactory;

    public List<Employee> findByFilters(String keyword, String grade, Integer storeId, int offset, int limit) {
        QEmployee e = QEmployee.employee;
        QStore s = QStore.store;
        BooleanBuilder builder = new BooleanBuilder();

        if (keyword != null && !keyword.isBlank()) {
            builder.and(e.name.containsIgnoreCase(keyword)
                    .or(e.grade.containsIgnoreCase(keyword)));
        }
        if (grade != null && !"all".equals(grade)) {
            builder.and(e.grade.eq(grade));
        }
        if (storeId != null && storeId > 0) {
            builder.and(e.store.id.eq(storeId));
        }
        return queryFactory.selectFrom(e)
                .leftJoin(e.store, s).fetchJoin()
                .where(builder)
                .orderBy(e.id.desc())
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    public long countByFilters(String keyword, String grade, Integer storeId) {
        QEmployee e = QEmployee.employee;
        BooleanBuilder builder = new BooleanBuilder();
        if (keyword != null && !keyword.isBlank()) {
            builder.and(e.name.containsIgnoreCase(keyword)
                    .or(e.grade.containsIgnoreCase(keyword)));
        }
        if (grade != null && !"all".equals(grade)) {
            builder.and(e.grade.eq(grade));
        }
        if (storeId != null && storeId > 0) {
            builder.and(e.store.id.eq(storeId));
        }
        return queryFactory.selectFrom(e).where(builder).fetchCount();
    }
}