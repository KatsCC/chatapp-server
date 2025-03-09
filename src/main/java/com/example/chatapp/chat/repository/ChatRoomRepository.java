package com.example.chatapp.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.chatapp.chat.entity.ChatRoom;
import com.example.chatapp.user.entity.User;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findByUsersContaining(User user);
}
