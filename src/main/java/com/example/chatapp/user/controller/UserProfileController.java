package com.example.chatapp.user.controller;

import com.example.chatapp.user.dto.PushTokenRequestDto;
import com.example.chatapp.user.dto.UserProfileDto;
import com.example.chatapp.user.entity.User;
import com.example.chatapp.user.service.UserService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
public class UserProfileController {

    private final UserService userService;

    @Autowired
    public UserProfileController(UserService userService) {
        this.userService = userService;
    }

    // 회원 정보 확인
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        String email = authentication.getName();
        User user = userService.findByEmailWithFriends(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // UserProfileDto로 변환하여 반환
        UserProfileDto userProfile = new UserProfileDto(user.getId(), user.getUsername(), user.getEmail(), user.getMention());
        return ResponseEntity.ok(userProfile);
    }

    // 멘션 수정
    @PutMapping("/me/mention")
    public ResponseEntity<?> updateMention(@RequestBody Map<String, String> request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        String email = authentication.getName();
        User user = userService.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        String mention = request.get("mention");
        if (mention == null) {
            return ResponseEntity.badRequest().body("Mention is required");
        }

        user.setMention(mention);
        userService.update(user);

        return ResponseEntity.ok("Mention updated successfully");
    }

    // push-token 등록
    @PostMapping("/push-token")
    public ResponseEntity<?> registerPushToken(@RequestBody PushTokenRequestDto request) {
        userService.registerPushToken(request.getEmail(), request.getExpoPushToken());
        return ResponseEntity.ok("Push token registered successfully");
    }
}


