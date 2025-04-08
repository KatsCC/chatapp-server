package com.example.chatapp.chat.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.chatapp.chat.dto.ChatRoomDto;
import com.example.chatapp.chat.entity.ChatRoom;
import com.example.chatapp.chat.repository.ChatRoomRepository;
import com.example.chatapp.user.entity.User;
import com.example.chatapp.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    @Autowired
    public ChatRoomService(ChatRoomRepository chatRoomRepository,
                           UserRepository userRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;
    }

    public ChatRoom createChatRoom(String name, List<Long> userIds, User creator) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(name);

        Set<User> users = new HashSet<>(userRepository.findAllById(userIds));
        users.add(creator); // 채팅방 생성자 추가
        chatRoom.setUsers(users);

        return chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public List<ChatRoomDto> getUserChatRooms(User user) {
        List<ChatRoom> chatRooms = chatRoomRepository.findChatRoomsByUserId(user.getId());
        return chatRooms.stream()
                .map(ChatRoomDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ChatRoom getChatRoomById(Long chatRoomId) {
        return chatRoomRepository.findChatRoomWithUsers(chatRoomId)
                .orElseThrow(() -> new NoSuchElementException("Chat room not found"));
    }
    
    public void inviteUser(Long chatRoomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new NoSuchElementException("Chat room not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        chatRoom.addUser(user);

        chatRoomRepository.save(chatRoom);
    }
}
