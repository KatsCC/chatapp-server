package com.example.chatapp.user.repository;

import com.example.chatapp.user.entity.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    @Query("select u from User u left join fetch u.friends where u.email = :email")
    User findByEmailWithFriends(@Param("email") String email);

    User findByEmail(String email);

    List<User> findByEmailContainingIgnoreCase(String email);
}