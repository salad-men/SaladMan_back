package com.kosta.saladMan.service.menu;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.kosta.saladMan.dto.menu.TotalMenuDto;
import com.kosta.saladMan.entity.menu.TotalMenu;
import com.kosta.saladMan.repository.MenuRepository;
import com.kosta.saladMan.util.PageInfo;
@Service
public class SMenuServiceImpl implements SMenuService {
	
	@Autowired
	private MenuRepository menuRepository;

	@Override
	public List<TotalMenuDto> getTotalMenu(PageInfo pageInfo, String sort) throws Exception {
		Sort sorting = Sort.by(Sort.Direction.ASC, "createdAt"); // 기본값
		
		if ("release_desc".equals(sort)) {
	        sorting = Sort.by(Sort.Direction.DESC, "createdAt");
	    } else if ("release_asc".equals(sort)) {
	        sorting = Sort.by(Sort.Direction.ASC, "createdAt");
	    } else if ("name_desc".equals(sort)) {
	        sorting = Sort.by(Sort.Direction.DESC, "name");
	    } else if ("name_asc".equals(sort)) {
	        sorting = Sort.by(Sort.Direction.ASC, "name");
	    } else if ("price_desc".equals(sort)) {
	        sorting = Sort.by(Sort.Direction.DESC, "salePrice");
	    } else if ("price_asc".equals(sort)) {
	        sorting = Sort.by(Sort.Direction.ASC, "salePrice");
	    }
		
		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 10, sorting);
		Page<TotalMenu> pages = menuRepository.findAll(pageRequest);
		
		pageInfo.setAllPage(pages.getTotalPages());
		int startPage = (pageInfo.getAllPage() - 1) / 10 * 10 + 1;
		int endPage = Math.min(startPage + 9, pageInfo.getAllPage());
		pageInfo.setStartPage(startPage);
		pageInfo.setEndPage(endPage);

	    return pages.getContent()
	            .stream()
	            .map(TotalMenuDto::fromEntity)
	            .collect(Collectors.toList());
	}

}
