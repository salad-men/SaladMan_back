package com.kosta.saladMan.dto.menu;

import com.kosta.saladMan.entity.menu.TotalMenu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BestMenuDto {
	private Integer id;
	private String name;
	private String img;
	private Long totalQuantity;

	public TotalMenu toEntity() {
		TotalMenu menu = new TotalMenu();
		menu.setId(this.id);
		menu.setName(this.name);
		menu.setImg(this.img);
		return menu;
	}
}
