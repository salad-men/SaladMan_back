package com.kosta.saladMan.repository.empolyee;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Repository;

import com.kosta.saladMan.dto.store.ScheduleDto;
import com.kosta.saladMan.entity.store.Employee;
import com.kosta.saladMan.entity.store.QEmployee;
import com.kosta.saladMan.entity.store.QSchedule;
import com.kosta.saladMan.entity.store.QStore;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
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
    
    // 주간 근무표 조회
    public List<ScheduleDto> findWeekSchedulesByStore(Integer storeId, Integer weekNo) {
        int year = LocalDate.now().getYear();  // 현재 연도를 가져옵니다.

        QSchedule s = QSchedule.schedule;
        QEmployee e = QEmployee.employee;
        QStore st = QStore.store;

        LocalDate weekStart = getWeekStart(year, weekNo);
        LocalDate weekEnd = weekStart.plusDays(6);

        BooleanBuilder builder = new BooleanBuilder();
        if (storeId != null && storeId > 0)
            builder.and(s.employee.store.id.eq(storeId));
        builder.and(s.workDate.between(weekStart, weekEnd));

        return queryFactory
                .select(Projections.constructor(
                        ScheduleDto.class,
                        s.id,
                        s.employee.id,
                        s.workDate,
                        s.shiftType
                ))
                .from(s)
                .leftJoin(s.employee, e)
                .leftJoin(e.store, st)
                .where(builder)
                .orderBy(s.workDate.asc(), e.name.asc())
                .fetch();
    }

    // year와 weekNo로 월요일 시작 주간의 첫날을 구함
    private LocalDate getWeekStart(int year, int weekNo) {
        WeekFields weekFields = WeekFields.of(Locale.KOREA); // 월요일 시작
        LocalDate firstDayOfYear = LocalDate.of(year, 1, 4); // ISO 8601 기준 1월 4일이 1주차에 항상 포함됨
        LocalDate weekStart = firstDayOfYear
            .with(weekFields.weekOfYear(), weekNo)
            .with(weekFields.dayOfWeek(), 1); // 1: 월요일
        return weekStart;
    }

}