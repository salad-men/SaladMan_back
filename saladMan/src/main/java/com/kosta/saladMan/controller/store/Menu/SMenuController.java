package com.kosta.saladMan.controller.store.Menu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.saladMan.auth.PrincipalDetails;
import com.kosta.saladMan.dto.menu.TotalMenuDto;
import com.kosta.saladMan.service.menu.SMenuService;
import com.kosta.saladMan.util.PageInfo;

@RestController
@RequestMapping("/store")
public class SMenuController {
	
	@Autowired
	private SMenuService menuService;
	
	// 전체 메뉴 조회
	@GetMapping("/totalMenu")
	public ResponseEntity<Map<String,Object>> getTotalMenu(@RequestParam(required = false) Map<String,String> param) {
		String sort = null;
		PageInfo pageInfo = new PageInfo(1);
		if(param != null) {
			if(param.get("page")!=null) {
				pageInfo.setCurPage(Integer.parseInt(param.get("page")));
			}
			sort = param.get("sort");
		}
		
	    try {
	        List<TotalMenuDto> totalMenu = menuService.getTotalMenu(pageInfo, sort);
	        Map<String, Object> res = new HashMap<>();
	        res.put("menus", totalMenu);
	        res.put("pageInfo", pageInfo);
	        res.put("sort", sort);
	        return new ResponseEntity<>(res, HttpStatus.OK);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }
	}
	
//	@GetMapping("/storeStatus")
//	public ResponseEntity<List<TotalMenuDto>> getMenus(Authentication authentication) {
//	    PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
//	    Integer storeId = principal.getStore().getId();
//	    return ResponseEntity.ok(menuService.getStoreStatus(storeId));
//	}


}
