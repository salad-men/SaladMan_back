package com.kosta.saladMan.entity.notice;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "announce")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Announce {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDate postedAt;

    private Integer viewCnt;

    private String img;

    private String type;  // 공지, 이벤트, 칭찬매장

    public void setViewCnt(Integer viewCnt) {
        this.viewCnt = viewCnt;
    }
}