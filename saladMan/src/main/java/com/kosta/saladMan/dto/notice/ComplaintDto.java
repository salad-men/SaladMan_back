package com.kosta.saladMan.dto.notice;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

import com.kosta.saladMan.entity.notice.Complaint;
import com.kosta.saladMan.entity.store.Store;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ComplaintDto {
    private Integer id;
    private Integer storeId;
    private String storeName;          
    private String title;
    private String content;
    private LocalDate writerDate;
    private String writerEmail;
    private String writerNickname;
    private Boolean isRead;
    private Boolean isRelay;

    public Complaint toEntity() {
        return Complaint.builder()
                .id(id)
                .store(Store.builder().id(storeId).build())
                .title(title)
                .content(content)
                .writerDate(writerDate)
                .writerEmail(writerEmail)
                .writerNickname(writerNickname)
                .isRead(isRead)
                .isRelay(isRelay)
                .build();
    }
}
