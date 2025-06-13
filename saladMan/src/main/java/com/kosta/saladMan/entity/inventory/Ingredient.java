package com.kosta.saladMan.entity.inventory;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.kosta.saladMan.dto.inventory.IngredientDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "category_id")
    private IngredientCategory category;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String unit;

    @Column(nullable = false)
    private Boolean available;

    public IngredientDto toDto() {
        return IngredientDto.builder()
                .id(id)
                .categoryId(category != null ? category.getId() : null)
                .name(name)
                .unit(unit)
                .available(available)
                .build();
    }
}
