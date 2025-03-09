package com.example.chatapp.chat.dto;

import com.example.chatapp.chat.entity.ChatRoom;

import lombok.Data;

@Data
public class ChatRoomDto {
    private Long id;
    private String name;

    public static ChatRoomDto fromEntity(ChatRoom chatRoom) {
        ChatRoomDto dto = new ChatRoomDto();
        dto.setId(chatRoom.getId());
        dto.setName(chatRoom.getName());
        return dto;
    }
}
