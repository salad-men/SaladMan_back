package com.kosta.saladMan.repository.notice;

import com.kosta.saladMan.entity.notice.Complaint;
import com.kosta.saladMan.entity.notice.QComplaint;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ComplaintDslRepository {

	@Autowired
    private JPAQueryFactory queryFactory;

	// 목록 조회 (페이징 + 필터)
    public List<Complaint> findComplaintsByFilters(PageRequest pageRequest, Integer storeId, Boolean isHqRead, Boolean isStoreRead, Boolean isRelay, String keyword) {
        QComplaint complaint = QComplaint.complaint;
        BooleanBuilder builder = new BooleanBuilder();

        if (storeId != null) {
            builder.and(complaint.store.id.eq(storeId));
        }
        if (isHqRead != null) builder.and(complaint.isHqRead.eq(isHqRead));
        if (isStoreRead != null) builder.and(complaint.isStoreRead.eq(isStoreRead));
        if (isRelay != null) {
            builder.and(complaint.isRelay.eq(isRelay));
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            builder.and(complaint.title.containsIgnoreCase(keyword));
        }

        return queryFactory.selectFrom(complaint)
                .where(builder)
                .orderBy(complaint.id.desc())
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();
    }

    // 전체 개수 조회
    public Long countComplaintsByFilters(Integer storeId, Boolean isHqRead, Boolean isStoreRead, Boolean isRelay, String keyword) {
        QComplaint complaint = QComplaint.complaint;
        BooleanBuilder builder = new BooleanBuilder();

        if (storeId != null) {
            builder.and(complaint.store.id.eq(storeId));
        }
        if (isHqRead != null) builder.and(complaint.isHqRead.eq(isHqRead));
        if (isStoreRead != null) builder.and(complaint.isStoreRead.eq(isStoreRead));
        if (isRelay != null) {
            builder.and(complaint.isRelay.eq(isRelay));
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            builder.and(complaint.title.containsIgnoreCase(keyword));
        }

        Long count = queryFactory.select(complaint.count())
                .from(complaint)
                .where(builder)
                .fetchOne();

        return count == null ? 0L : count;
    }
}
