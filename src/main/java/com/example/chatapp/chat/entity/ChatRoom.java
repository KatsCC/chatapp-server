package com.example.chatapp.chat.entity;

import java.util.HashSet;
import java.util.Set;

import com.example.chatapp.user.entity.User;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "chat_rooms")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(
        name = "chat_room_users",
        joinColumns = @JoinColumn(name = "chat_room_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users = new HashSet<>();
    
    public void addUser(User user) {
        this.users.add(user);
        user.getChatRooms().add(this);
    }

    public void removeUser(User user) {
        this.users.remove(user);
        user.getChatRooms().remove(this);
    }
}
