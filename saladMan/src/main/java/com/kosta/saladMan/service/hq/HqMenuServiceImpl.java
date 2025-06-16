package com.kosta.saladMan.service.hq;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.kosta.saladMan.dto.menu.TotalMenuDto;
import com.kosta.saladMan.entity.menu.TotalMenu;
import com.kosta.saladMan.repository.MenuRepository;
@Service
public class HqMenuServiceImpl implements HqMenuService {
	
	@Autowired
	private MenuRepository menuRepository;

	@Override
	public List<TotalMenuDto> getAllMenus(String sort) throws Exception {
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

	    return menuRepository.findAll(sorting)
	            .stream()
	            .map(TotalMenuDto::fromEntity)
	            .collect(Collectors.toList());
	}

}
