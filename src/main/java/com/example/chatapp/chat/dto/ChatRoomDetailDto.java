package com.example.chatapp.chat.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.example.chatapp.chat.entity.ChatRoom;
import com.example.chatapp.user.dto.UserDto;

import lombok.Data;

@Data
public class ChatRoomDetailDto {
    private Long id;
    private String name;
    private List<UserDto> users;

    public static ChatRoomDetailDto fromEntity(ChatRoom chatRoom) {
        ChatRoomDetailDto dto = new ChatRoomDetailDto();
        dto.setId(chatRoom.getId());
        dto.setName(chatRoom.getName());
        dto.setUsers(chatRoom.getUsers().stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList()));
        return dto;
    }
}
