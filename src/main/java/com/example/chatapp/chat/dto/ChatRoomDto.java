package com.example.chatapp.chat.dto;

import com.example.chatapp.chat.entity.ChatRoom;

import com.example.chatapp.user.dto.UserSimpleDto;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class ChatRoomDto {
    private Long id;
    private String name;

    private List<UserSimpleDto> participants;

    public static ChatRoomDto fromEntity(ChatRoom chatRoom) {
        ChatRoomDto dto = new ChatRoomDto();
        dto.setId(chatRoom.getId());
        dto.setName(chatRoom.getName());
        dto.setParticipants(chatRoom.getUsers().stream()
                .map(UserSimpleDto::fromEntity)
                .collect(Collectors.toList()));
        return dto;
    }
}
