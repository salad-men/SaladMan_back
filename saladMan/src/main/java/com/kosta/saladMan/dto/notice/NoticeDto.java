package com.kosta.saladMan.dto.notice;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

import com.kosta.saladMan.entity.notice.Notice;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeDto {
    private Integer id;
    private String title;
    private String content;
    private LocalDate postedAt;
    private Integer viewCnt;
    private String imgFileName;
    private String fileName;
    private String fileOriginName;

    public Notice toEntity() {
        return Notice.builder()
                .id(id)
                .title(title)
                .content(content)
                .postedAt(postedAt)
                .viewCnt(viewCnt)
                .imgFileName(imgFileName)
                .fileName(fileName)
                .fileOriginName(fileOriginName)
                .build();
    }
}