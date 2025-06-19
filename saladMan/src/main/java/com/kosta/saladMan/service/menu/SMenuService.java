package com.kosta.saladMan.service.menu;

import java.util.List;

import com.kosta.saladMan.dto.menu.TotalMenuDto;
import com.kosta.saladMan.util.PageInfo;

public interface SMenuService {
	List<TotalMenuDto> getTotalMenu(PageInfo pageInfo, String sort) throws Exception;
//	List<TotalMenuDto> getStoreStuatus(Integer id) throws Exception;
}
