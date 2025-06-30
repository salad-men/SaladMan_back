package com.kosta.saladMan.entity.store;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import com.kosta.saladMan.dto.store.StoreDto;
import com.kosta.saladMan.entity.chat.ChatMessage;
import com.kosta.saladMan.entity.chat.ChatParticipant;
import com.kosta.saladMan.entity.chat.ReadStatus;

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
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;  // sid

    @Column(nullable = false)
    private String name;  // sname

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String address;

    private Double latitude;

    private Double longitude;

    private String phoneNumber;  // number

    private String openTime;

    private String closeTime;

    private String breakDay;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDate closedAt;

    private Boolean autoOrderEnabled;

    private String username; //계정 id

    private String password;

    private Integer deliveryDay;

    private String fcmToken;

    private String role;
    
    // Chat 관계 (양방향, 삭제 cascade, orphan, 추후 리팩토링)
    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ChatMessage> chatMessages = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ChatParticipant> chatParticipants = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ReadStatus> readStatuses = new ArrayList<>();

    public StoreDto toDto() {
        return StoreDto.builder()
                .id(id)
                .name(name)
                .location(location)
                .address(address)
                .latitude(latitude)
                .longitude(longitude)
                .phoneNumber(phoneNumber)
                .openTime(openTime)
                .closeTime(closeTime)
                .breakDay(breakDay)
                .createdAt(createdAt)
                .closedAt(closedAt)
                .autoOrderEnabled(autoOrderEnabled)
                .username(username)
                .password(password)
                .deliveryDay(deliveryDay)
                .fcmToken(fcmToken)
                .role(role)
                .build();
    }
}
