package com.example.chatapp.chat.dto;

import java.util.List;

import lombok.Data;

@Data
public class ChatRoomCreateRequest {
    private String name;
    private List<Long> userIds;
}
