package com.example.chatapp.chat.entity;

import java.time.LocalDateTime;

import com.example.chatapp.user.entity.User;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "chat_messages")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    private String content;

    private LocalDateTime timestamp = LocalDateTime.now();
}
