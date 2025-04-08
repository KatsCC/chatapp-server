package com.example.chatapp.user.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PushNotificationService {
    private final RestTemplate restTemplate = new RestTemplate();
    
    public void sendPushNotification(String expoPushToken, String title, String body) {
        if (expoPushToken == null || expoPushToken.isEmpty()) {
            return;
        }
        String url = "https://exp.host/--/api/v2/push/send";

        Map<String, Object> payload = new HashMap<>();
        payload.put("to", expoPushToken);
        payload.put("title", title);
        payload.put("body", body);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            System.out.println("푸시 알림 응답: " + response.getBody());
        } catch (Exception e) {
            System.err.println("푸시 알림 전송 실패: " + e.getMessage());
        }
    }
}
