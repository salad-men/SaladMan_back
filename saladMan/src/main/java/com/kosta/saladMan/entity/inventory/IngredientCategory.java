package com.kosta.saladMan.entity.inventory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.kosta.saladMan.dto.inventory.IngredientCategoryDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    public IngredientCategoryDto toDto() {
        return IngredientCategoryDto.builder()
                .id(id)
                .name(name)
                .build();
    }
}
