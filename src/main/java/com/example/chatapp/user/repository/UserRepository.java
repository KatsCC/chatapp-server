package com.example.chatapp.user.repository;

import com.example.chatapp.user.entity.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    // 이메일로 사용자 조회
    User findByEmail(String email);

    // 이메일이 유사한 사용자 목록 조회 (이메일 주소에 특정 문자열이 포함된 사용자 검색)
    List<User> findByEmailContainingIgnoreCase(String email);
}