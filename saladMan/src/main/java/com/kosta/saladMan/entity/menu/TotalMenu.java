package com.kosta.saladMan.entity.menu;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@DynamicInsert
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TotalMenu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;  // menu_id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "category_id")
    private MenuCategory category;

    @Column(nullable = false)
    private String name;  // mname

    private Integer originPrice;

    private Integer salePrice;

    private String img;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;  // desc -> description 으로 변경

    @CreationTimestamp
    private LocalDate createdAt;
}
