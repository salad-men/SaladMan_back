package com.kosta.saladMan.service.user;

import com.kosta.saladMan.dto.menu.TotalMenuDto;
import java.util.List;

public interface MenuService {
    List<TotalMenuDto> getMenusByCategory(Integer categoryId);
}
