package com.kosta.saladMan.entity.notice;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Lob;

import org.hibernate.annotations.DynamicInsert;

import com.kosta.saladMan.dto.notice.ComplaintDto;
import com.kosta.saladMan.entity.store.Store;

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
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "store_id")
    private Store store;

    private String title;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDate writerDate;

    private String writerEmail;

    private String writerNickname;
    
    private Boolean isRead;
    
    private Boolean isRelay;
    
    public ComplaintDto toDto() {
        return ComplaintDto.builder()
                .id(id)
                .storeId(store.getId())
                .storeName(store.getName())
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
