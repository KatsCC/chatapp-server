package com.example.chatapp.friend.controller;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.chatapp.friend.entity.FriendRequest;
import com.example.chatapp.user.dto.UserProfileDto;
import com.example.chatapp.user.entity.User;
import com.example.chatapp.user.service.UserService;

@RestController
@RequestMapping("/friends")
public class FriendController {

    private final UserService userService;

    @Autowired
    public FriendController(UserService userService) {
        this.userService = userService;
    }

    // 이메일로 사용자 검색
    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestParam String email) {
        List<User> users = userService.searchUsersByEmail(email);

        List<UserProfileDto> userProfiles = users.stream()
                .map(user -> new UserProfileDto(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getMention()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(userProfiles);
    }

    // 친구 요청 보내기
    @PostMapping("/request")
    public ResponseEntity<?> sendFriendRequest(@RequestBody Map<String, Long> request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User sender = userService.findByEmailWithFriends(email);

        Long recipientId = request.get("recipientId");
        if (recipientId == null) {
            return ResponseEntity.badRequest().body("Recipient ID is required");
        }

        User recipient = userService.findById(recipientId);
        if (recipient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        try {
            userService.sendFriendRequest(sender, recipient);
            return ResponseEntity.ok("Friend request sent");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 받은 친구 요청 목록 조회
    @GetMapping("/requests")
    public ResponseEntity<?> getFriendRequests() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User recipient = userService.findByEmail(email);

        List<FriendRequest> requests = userService.getReceivedFriendRequests(recipient);

        List<Map<String, Object>> response = requests.stream().map(request -> {
            Map<String, Object> map = new HashMap<>();
            map.put("requestId", request.getId());
            map.put("senderId", request.getSender().getId());
            map.put("senderUsername", request.getSender().getUsername());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // 친구 요청 수락
    @PostMapping("/requests/{requestId}/accept")
    public ResponseEntity<?> acceptFriendRequest(@PathVariable Long requestId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User recipient = userService.findByEmailWithFriends(email);

        Optional<FriendRequest> optionalRequest = userService.findFriendRequestById(requestId);
        if (!optionalRequest.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Friend request not found");
        }

        FriendRequest friendRequest = optionalRequest.get();

        // 친구 요청의 수신자가 현재 사용자와 동일한지 확인
        if (!friendRequest.getRecipient().equals(recipient)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to accept this request");
        }

        userService.acceptFriendRequest(friendRequest);

        return ResponseEntity.ok("Friend request accepted");
    }

    // 친구 목록 가져오기
    @GetMapping("/list")
    public ResponseEntity<?> getFriends() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.findByEmailWithFriends(email);

        List<User> friends = userService.getFriends(user);

        List<UserProfileDto> friendProfiles = friends.stream()
                .map(friend -> new UserProfileDto(
                    friend.getId(),
                    friend.getUsername(),
                    friend.getEmail(),
                    friend.getMention()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(friendProfiles);
    }
}
