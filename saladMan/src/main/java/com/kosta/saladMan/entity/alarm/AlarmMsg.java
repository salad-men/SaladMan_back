package com.kosta.saladMan.entity.alarm;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.Lob;

import org.hibernate.annotations.DynamicInsert;

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
public class AlarmMsg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    private String type;

    private String note;
}
