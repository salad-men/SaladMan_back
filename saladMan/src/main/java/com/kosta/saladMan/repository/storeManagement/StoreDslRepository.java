package com.kosta.saladMan.repository.storeManagement;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.dto.store.StoreDto;
import com.kosta.saladMan.entity.store.QStore;
import com.kosta.saladMan.entity.store.Store;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class StoreDslRepository {
	
    @Autowired
    private JPAQueryFactory jpaQueryFactory;
    
    @Autowired
    private ModelMapper modelMapper;
    
    public Page<StoreDto> searchStores(String location, String status, String keyword, Pageable pageable) {
        QStore store = QStore.store;

        BooleanBuilder where = new BooleanBuilder();
        where.and(store.role.eq("ROLE_STORE"));

        if (location != null && !location.equals("전체 지역")) {
            where.and(store.location.eq(location));
        }

        if (status != null && !status.equals("매장상태")) {
            if (status.equals("운영중")) {
                where.and(store.closedAt.isNull());
            } else if (status.equals("운영종료")) {
                where.and(store.closedAt.isNotNull());
            }
        }

        if (keyword != null && !keyword.isBlank()) {
            where.and(store.name.containsIgnoreCase(keyword)
            		.or(store.username.containsIgnoreCase(keyword))
            		.or(store.address.containsIgnoreCase(keyword))
            		.or(store.phoneNumber.containsIgnoreCase(keyword))
            );
        }

        List<Store> results = jpaQueryFactory
                .selectFrom(store)
                .where(where)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        
        long total = jpaQueryFactory
                .select(store.count())
                .from(store)
                .where(where)
                .fetchOne();

        List<StoreDto> storeList = results.stream()
                .map(s -> modelMapper.map(s, StoreDto.class))
                .collect(Collectors.toList());

        return new PageImpl<>(storeList, pageable, total);
    }
    
    public List<String> findStoreNamesByLocation(String location) {
        QStore store = QStore.store;

        return jpaQueryFactory
                .select(store.name)
                .from(store)
                .where(store.address.contains(location))
                .orderBy(store.name.asc())
                .fetch();
    }
}
