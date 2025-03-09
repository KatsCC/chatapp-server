package com.example.chatapp.chat.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.chatapp.chat.dto.ChatRoomCreateRequest;
import com.example.chatapp.chat.dto.ChatRoomDetailDto;
import com.example.chatapp.chat.dto.ChatRoomDto;
import com.example.chatapp.chat.entity.ChatRoom;
import com.example.chatapp.chat.service.ChatRoomService;
import com.example.chatapp.user.entity.User;
import com.example.chatapp.user.service.UserService;

@RestController
@RequestMapping("/api/chat")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final UserService userService;

    @Autowired
    public ChatRoomController(ChatRoomService chatRoomService,
                              UserService userService) {
        this.chatRoomService = chatRoomService;
        this.userService = userService;
    }

    // 채팅방 생성
    @PostMapping("/rooms")
    public ResponseEntity<?> createChatRoom(@RequestBody ChatRoomCreateRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User creator = userService.findByEmail(email);

        ChatRoom chatRoom = chatRoomService.createChatRoom(request.getName(), request.getUserIds(), creator);

        return ResponseEntity.ok(ChatRoomDto.fromEntity(chatRoom));
    }

    // 사용자의 채팅방 목록 조회
    @GetMapping("/rooms")
    public ResponseEntity<?> getUserChatRooms() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.findByEmail(email);

        List<ChatRoomDto> chatRooms = chatRoomService.getUserChatRooms(user);

        return ResponseEntity.ok(chatRooms);
    }

    // 채팅방 정보 조회
    @GetMapping("/rooms/{chatRoomId}")
    public ResponseEntity<?> getChatRoomInfo(@PathVariable Long chatRoomId) {

        ChatRoom chatRoom = chatRoomService.getChatRoomById(chatRoomId);

        return ResponseEntity.ok(ChatRoomDetailDto.fromEntity(chatRoom));
    }

    // 채팅방에 사용자 초대
    @PostMapping("/rooms/{chatRoomId}/invite")
    public ResponseEntity<?> inviteUser(
            @PathVariable Long chatRoomId,
            @RequestBody Map<String, Long> request) {

        Long userId = request.get("userId");

        if (userId == null) {
            return ResponseEntity.badRequest().body("User ID is required");
        }

        chatRoomService.inviteUser(chatRoomId, userId);

        return ResponseEntity.ok("User invited successfully");
    }
}
