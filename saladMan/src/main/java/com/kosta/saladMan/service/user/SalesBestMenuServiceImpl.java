package com.kosta.saladMan.service.user;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.kosta.saladMan.dto.menu.BestMenuDto;
import com.kosta.saladMan.repository.user.SalesBestMenuRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SalesBestMenuServiceImpl implements SalesBestMenuService {

    private final SalesBestMenuRepository repository;

    @Override
    public List<BestMenuDto> getBestMenus() {
        return repository.findBestMenusNative().stream()
            .map(row -> new BestMenuDto(
            	    ((Number) row[0]).intValue(),
            	    (String) row[1],
            	    (String) row[2],
            	    ((Number) row[3]).longValue()
            	))
            .collect(Collectors.toList());
    }
}