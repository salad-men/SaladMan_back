package com.kosta.saladMan.dto.alarm;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

import com.kosta.saladMan.entity.alarm.AlarmMsg;

import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmMsgDto {
    private Integer id;
    private String title;
    private String content;
    private String type;
    private String note;

    public AlarmMsg toEntity() {
        return AlarmMsg.builder()
                .id(id)
                .title(title)
                .content(content)
                .type(type)
                .note(note)
                .build();
    }
}
