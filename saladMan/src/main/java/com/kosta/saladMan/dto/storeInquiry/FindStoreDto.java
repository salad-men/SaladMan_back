package com.kosta.saladMan.dto.storeInquiry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindStoreDto {
	private Integer id;
	private String name;
    private String location;
    private Double latitude;
    private Double longitude;
}
