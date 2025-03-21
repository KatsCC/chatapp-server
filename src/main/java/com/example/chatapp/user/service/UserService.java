package com.example.chatapp.user.service;

import com.example.chatapp.friend.entity.FriendRequest;
import com.example.chatapp.friend.entity.FriendRequest.FriendRequestStatus;
import com.example.chatapp.friend.repository.FriendRequestRepository;
import com.example.chatapp.user.dto.UserDto;
import com.example.chatapp.user.entity.User;
import com.example.chatapp.user.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FriendRequestRepository friendRequestRepository;

    @Autowired
    public UserService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        FriendRequestRepository friendRequestRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.friendRequestRepository = friendRequestRepository;
    }

    public User save(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEmail(userDto.getEmail());
        user.setRoles("ROLE_USER");

        return userRepository.save(user);
    }

    public User update(User user) {
        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> searchUsersByEmail(String email) {
        return userRepository.findByEmailContainingIgnoreCase(email);
    }

    // 친구 요청 보내기
    public void sendFriendRequest(User sender, User recipient) {
        // 이미 친구인지 확인
        if (sender.getFriends().contains(recipient)) {
            throw new IllegalStateException("Already friends with this user.");
        }

        // 이미 요청이 존재하는지 확인
        FriendRequest existingRequest = friendRequestRepository.findBySenderAndRecipient(sender, recipient);
        if (existingRequest != null) {
            throw new IllegalStateException("Friend request already sent.");
        }

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSender(sender);
        friendRequest.setRecipient(recipient);
        friendRequest.setStatus(FriendRequestStatus.PENDING);

        friendRequestRepository.save(friendRequest);
    }

    // 친구 요청 수락
    @Transactional
    public void acceptFriendRequest(FriendRequest friendRequest) {

        System.out.println("Accepting friend request: " + friendRequest.getId());
        friendRequest.setStatus(FriendRequestStatus.ACCEPTED);

        // 양방향 친구 관계 설정
        User sender = friendRequest.getSender();
        User recipient = friendRequest.getRecipient();

        sender.addFriend(recipient);
        recipient.addFriend(sender);

        System.out.println("Sender: " + sender.getEmail());
        System.out.println("Recipient: " + recipient.getEmail());

        userRepository.save(sender);
        userRepository.save(recipient);
        friendRequestRepository.save(friendRequest);
    }

    // 친구 목록 가져오기
    public List<User> getFriends(User user) {
        return new ArrayList<>(user.getFriends());
    }

    // 받은 친구 요청 목록 가져오기
    public List<FriendRequest> getReceivedFriendRequests(User user) {
        return friendRequestRepository.findByRecipientAndStatus(user, FriendRequestStatus.PENDING);
    }

    public Optional<FriendRequest> findFriendRequestById(Long requestId) {
        return friendRequestRepository.findById(requestId);
    }
    
    // 사용자 ID로 사용자 찾기
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

//    public User findByEmailWithFriends(String email) {
//        User user = userRepository.findByEmailWithFriends(email);
//        if (user == null) {
//            throw new UsernameNotFoundException("User not found with email: " + email);
//        }
//        return user;
//    }

    // UserDetailsService의 메서드 구현
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailWithFriends(email);
    
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
    
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            new ArrayList<>()
        );
    }

    //Push토큰 등록
    public void registerPushToken(String email, String expoPushToken) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setExpoPushToken(expoPushToken);
            userRepository.save(user);
        }
    }
}