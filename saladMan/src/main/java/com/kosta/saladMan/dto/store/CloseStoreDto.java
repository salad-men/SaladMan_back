package com.kosta.saladMan.dto.store;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CloseStoreDto {
    private Integer id;
    private LocalDate closedAt;
}
