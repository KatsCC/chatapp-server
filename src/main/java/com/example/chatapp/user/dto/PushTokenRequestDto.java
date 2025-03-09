package com.example.chatapp.user.dto;

import lombok.Data;

@Data
public class PushTokenRequestDto {
    private String email;             
    private String expoPushToken;
}
