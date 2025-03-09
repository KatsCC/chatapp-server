package com.example.chatapp.friend.entity;

import com.example.chatapp.user.entity.User;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "friend_requests")
@Data
public class FriendRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 요청을 보낸 사용자
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    // 요청을 받은 사용자
    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private User recipient;

    // 요청 상태 (예: PENDING, ACCEPTED, DECLINED)
    @Enumerated(EnumType.STRING)
    private FriendRequestStatus status = FriendRequestStatus.PENDING;

    // 생성 시간 등을 관리하고 싶다면 추가 필드로 Timestamp를 넣을 수 있습니다.
    public enum FriendRequestStatus {
        PENDING,
        ACCEPTED,
        DECLINED
    }
}
