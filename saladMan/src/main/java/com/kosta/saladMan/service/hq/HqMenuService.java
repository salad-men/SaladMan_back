package com.kosta.saladMan.service.hq;

import java.util.List;

import com.kosta.saladMan.dto.menu.TotalMenuDto;

public interface HqMenuService {
	List<TotalMenuDto> getAllMenus(String sort) throws Exception;
	
}
