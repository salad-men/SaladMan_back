package com.kosta.saladMan.dto.store;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetStorePasswordDto {
    private Integer id;              // 매장 ID
    private String adminPassword; // 관리자 본인 비밀번호
    private String newPassword;   // 새 비밀번호
}
