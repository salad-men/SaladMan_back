package com.kosta.saladMan.entity.notice;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.Lob;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import com.kosta.saladMan.dto.notice.NoticeDto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@DynamicInsert
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    private LocalDate postedAt;

    private Integer viewCnt;

    private String imgFileName;
    private String fileName;
    private String fileOriginName;

    
    public NoticeDto toDto() {
        return NoticeDto.builder()
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
