package com.example.chatapp.chat.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.chatapp.chat.dto.ChatMessageDto;
import com.example.chatapp.chat.entity.ChatMessage;
import com.example.chatapp.chat.entity.ChatRoom;
import com.example.chatapp.chat.repository.ChatMessageRepository;
import com.example.chatapp.chat.repository.ChatRoomRepository;
import com.example.chatapp.user.entity.User;
import com.example.chatapp.user.repository.UserRepository;
import com.example.chatapp.user.service.PushNotificationService;

@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final PushNotificationService pushNotificationService;

    @Autowired
    public ChatMessageService(ChatMessageRepository chatMessageRepository,
                              ChatRoomRepository chatRoomRepository,
                              UserRepository userRepository,
                              PushNotificationService pushNotificationService) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.pushNotificationService = pushNotificationService;
    }

    public ChatMessage saveMessage(Long chatRoomId, User sender, String content) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new NoSuchElementException("Chat room not found"));
        
        if (!chatRoom.getUsers().contains(sender)) {
            throw new IllegalArgumentException("User not in chat room");
        }
        
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChatRoom(chatRoom);
        chatMessage.setSender(sender);
        chatMessage.setContent(content);
        
        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);
        
        // 푸시 알림 전송: 채팅방의 발신자 외의 모든 참여자에게 푸시 알림을 보냅니다.
        chatRoom.getUsers().stream()
            .filter(user -> !user.getId().equals(sender.getId()))
            .forEach(user -> {
                String expoPushToken = user.getExpoPushToken();
                if (expoPushToken != null && !expoPushToken.isEmpty()) {
                    pushNotificationService.sendPushNotification(
                        expoPushToken,
                        "새 메시지 도착",
                        "채팅방 " + chatRoom.getName() + "에 새로운 메시지가 있습니다."
                    );
                }
            });
        
        return savedMessage;
    }

    // 최신 메시지 불러오기
    public List<ChatMessageDto> getRecentMessages(Long chatRoomId, Long lastMessageId) {
        List<ChatMessage> messages;
        if (lastMessageId == null) {
            messages = chatMessageRepository.findTop30ByChatRoomIdOrderByIdDesc(chatRoomId);
        } else {
            messages = chatMessageRepository.findTop30ByChatRoomIdAndIdGreaterThanOrderByIdAsc(chatRoomId, lastMessageId);
        }
        return messages.stream()
                .map(ChatMessageDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 이전 메시지 불러오기
    public List<ChatMessageDto> getPreviousMessages(Long chatRoomId, Long firstMessageId) {
        List<ChatMessage> messages = chatMessageRepository.findTop30ByChatRoomIdAndIdLessThanOrderByIdDesc(chatRoomId, firstMessageId);
        return messages.stream()
                .map(ChatMessageDto::fromEntity)
                .collect(Collectors.toList());
    }
}
