package com.kosta.saladMan.dto;

public class UserDto {
    private Long id;
    private String name;

    public UserDto() {}

    public UserDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // getter, setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}