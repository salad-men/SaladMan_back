package com.kosta.saladMan.dto.alarm;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.kosta.saladMan.entity.alarm.Alarm;
import com.kosta.saladMan.entity.store.Store;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmDto {
    private Integer id;
    private Integer storeId;
    private Boolean isRead;
    private String title;
    private String content;
    private LocalDateTime sendAt;

    public Alarm toEntity() {
        return Alarm.builder()
                .id(id)
                .store(Store.builder().id(storeId).build())
                .isRead(isRead)
                .title(title)
                .content(content)
                .sendAt(sendAt)
                .build();
    }
}
