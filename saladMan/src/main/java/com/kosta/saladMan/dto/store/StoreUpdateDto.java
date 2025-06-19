package com.kosta.saladMan.dto.store;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.kosta.saladMan.entity.store.Store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreUpdateDto {
    private Integer id;
    private String name;
    private String location;
    private String address;
    private Double latitude;
    private Double longitude;
    private String phoneNumber;
    private String openTime;
    private String closeTime;
    private String breakDay;
    private LocalDateTime createdAt;
    private LocalDate closedAt;
    private Boolean autoOrderEnabled;
    private String username;
    private Integer deliveryDay;
    
    public Store toEntity() {
        return Store.builder()
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
                .deliveryDay(deliveryDay)
                .build();
    }
    
    public static StoreUpdateDto fromEntity(Store store) {
        if (store == null) return null;

        return StoreUpdateDto.builder()
                .id(store.getId())
                .name(store.getName())
                .location(store.getLocation())
                .address(store.getAddress())
                .latitude(store.getLatitude())
                .longitude(store.getLongitude())
                .phoneNumber(store.getPhoneNumber())
                .openTime(store.getOpenTime())
                .closeTime(store.getCloseTime())
                .breakDay(store.getBreakDay())
                .createdAt(store.getCreatedAt())
                .closedAt(store.getClosedAt())
                .autoOrderEnabled(store.getAutoOrderEnabled())
                .username(store.getUsername())
                .deliveryDay(store.getDeliveryDay())
                .build();
    }
}
