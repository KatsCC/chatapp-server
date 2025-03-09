package com.example.chatapp.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.chatapp.chat.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 최신 메시지 불러오기
    List<ChatMessage> findTop30ByChatRoomIdOrderByIdDesc(Long chatRoomId);

    // 특정 메시지 ID 이후의 메시지 불러오기
    List<ChatMessage> findTop30ByChatRoomIdAndIdGreaterThanOrderByIdAsc(Long chatRoomId, Long lastMessageId);

    // 특정 메시지 ID 이전의 메시지 불러오기
    List<ChatMessage> findTop30ByChatRoomIdAndIdLessThanOrderByIdDesc(Long chatRoomId, Long firstMessageId);
}
