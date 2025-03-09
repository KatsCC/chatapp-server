package com.example.chatapp.chat.dto;

import java.time.LocalDateTime;

import com.example.chatapp.chat.entity.ChatMessage;

import lombok.Data;

@Data
public class ChatMessageDto {
    private Long id;
    private Long chatRoomId;
    private Long senderId;
    private String senderName;
    private String content;
    private LocalDateTime timestamp;

    public static ChatMessageDto fromEntity(ChatMessage message) {
        ChatMessageDto dto = new ChatMessageDto();
        dto.setId(message.getId());
        dto.setChatRoomId(message.getChatRoom().getId());
        dto.setSenderId(message.getSender().getId());
        dto.setSenderName(message.getSender().getUsername());
        dto.setContent(message.getContent());
        dto.setTimestamp(message.getTimestamp());
        return dto;
    }
}
