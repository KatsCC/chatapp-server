package com.example.chatapp.chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.chatapp.chat.entity.ChatRoom;
import com.example.chatapp.user.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("select distinct cr from ChatRoom cr " +
            "left join fetch cr.users " +
            "where :userId in (select u.id from cr.users u)")
    List<ChatRoom> findChatRoomsByUserId(@Param("userId") Long userId);

    @Query("select distinct cr from ChatRoom cr left join fetch cr.users where cr.id = :roomId")
    Optional<ChatRoom> findChatRoomWithUsers(@Param("roomId") Long roomId);

    List<ChatRoom> findByUsersContaining(User user);
}
