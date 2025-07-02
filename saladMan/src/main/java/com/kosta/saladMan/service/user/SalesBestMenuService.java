package com.kosta.saladMan.service.user;

import java.util.List;

import com.kosta.saladMan.dto.menu.BestMenuDto;

public interface SalesBestMenuService {
    List<BestMenuDto> getBestMenus();
}