package com.example.chatapp.user.dto;

import lombok.Data;

@Data
public class UserProfileDto {
    private Long id; 
    private String username;
    private String email;
    private String mention;

    public UserProfileDto(Long id,String username, String email, String mention) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.mention = mention;
    }
}
