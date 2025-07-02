package com.kosta.saladMan.dto.notice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import com.kosta.saladMan.entity.notice.Announce;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnounceDto {
    private Integer id;
    private String title;
    private String content;
    private LocalDate postedAt;
    private Integer viewCnt;
    private String img;
    private String type;
    
    public Announce toEntity() {
        return Announce.builder()
                .id(this.id)
                .title(this.title)
                .content(this.content)
                .postedAt(this.postedAt)
                .viewCnt(this.viewCnt)
                .img(this.img)
                .type(this.type)
                .build();
    }
}
