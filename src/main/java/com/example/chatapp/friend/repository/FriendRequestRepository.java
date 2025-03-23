package com.example.chatapp.friend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.chatapp.friend.entity.FriendRequest;
import com.example.chatapp.friend.entity.FriendRequest.FriendRequestStatus;
import com.example.chatapp.user.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    List<FriendRequest> findByRecipientAndStatus(User recipient, FriendRequestStatus status);
    List<FriendRequest> findBySenderAndStatus(User sender, FriendRequestStatus status);

    // 특정한 친구 요청을 찾기 위한 메서드
    FriendRequest findBySenderAndRecipient(User sender, User recipient);

    @Query("select fr from FriendRequest fr join fetch fr.recipient join fetch fr.sender where fr.id = :id")
    Optional<FriendRequest> findByIdWithUsers(@Param("id") Long id);
}
