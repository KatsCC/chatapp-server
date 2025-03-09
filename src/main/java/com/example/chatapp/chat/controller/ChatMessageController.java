package com.example.chatapp.chat.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.example.chatapp.chat.dto.ChatMessageDto;
import com.example.chatapp.chat.service.ChatMessageService;
import com.example.chatapp.user.service.UserService;

@RestController
@RequestMapping("/api/chat")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @Autowired
    public ChatMessageController(ChatMessageService chatMessageService,
                                 UserService userService) {
        this.chatMessageService = chatMessageService;
    }

    // 최신 메시지 불러오기
    @GetMapping("/rooms/{chatRoomId}/messages/recent")
    public ResponseEntity<?> getRecentMessages(
            @PathVariable Long chatRoomId,
            @RequestParam(required = false) Long lastMessageId) {

        List<ChatMessageDto> messages = chatMessageService.getRecentMessages(chatRoomId, lastMessageId);

        return ResponseEntity.ok(messages);
    }

    // 이전 메시지 불러오기
    @GetMapping("/rooms/{chatRoomId}/messages/previous")
    public ResponseEntity<?> getPreviousMessages(
            @PathVariable Long chatRoomId,
            @RequestParam Long firstMessageId) {

        List<ChatMessageDto> messages = chatMessageService.getPreviousMessages(chatRoomId, firstMessageId);

        return ResponseEntity.ok(messages);
    }
}
