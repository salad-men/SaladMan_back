package com.kosta.saladMan.dto.alarm;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendAlarmDto {
	private Integer id;
    private Integer storeId;
    private Boolean isRead;
    private String title;
    private String content;
    private LocalDateTime sendAt;
    private Integer alarmMsgId;

}
