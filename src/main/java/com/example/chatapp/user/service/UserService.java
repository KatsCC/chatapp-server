package com.example.chatapp.user.service;

import com.example.chatapp.friend.entity.FriendRequest;
import com.example.chatapp.friend.entity.FriendRequest.FriendRequestStatus;
import com.example.chatapp.friend.repository.FriendRequestRepository;
import com.example.chatapp.user.dto.UserDto;
import com.example.chatapp.user.dto.UserProfileDto;
import com.example.chatapp.user.entity.User;
import com.example.chatapp.user.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.hibernate.Hibernate;
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

    @Transactional
    public void sendFriendRequest(User sender, User recipient) {

        if (sender.getFriends().contains(recipient)) {
            throw new IllegalStateException("Already friends with this user.");
        }

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

    @Transactional
    public void acceptFriendRequest(FriendRequest friendRequest) {

        System.out.println("Accepting friend request: " + friendRequest.getId());
        friendRequest.setStatus(FriendRequestStatus.ACCEPTED);

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

    public List<User> getFriends(User user) {
        return new ArrayList<>(user.getFriends());
    }

    public List<FriendRequest> getReceivedFriendRequests(User user) {
        return friendRequestRepository.findByRecipientAndStatus(user, FriendRequestStatus.PENDING);
    }

    @Transactional
    public Optional<FriendRequest> findFriendRequestByIdWithUsers(Long requestId) {
        Optional<FriendRequest> optionalRequest = friendRequestRepository.findByIdWithUsers(requestId);
        optionalRequest.ifPresent(fr -> {
            Hibernate.initialize(fr.getRecipient());
            Hibernate.initialize(fr.getSender());
        });
        return optionalRequest;
    }


    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public User findByEmailWithFriends(String email) {
        User user = userRepository.findByEmailWithFriends(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        System.out.println("Number of friends: " + user.getFriends().size());
        return user;
    }

    @Transactional
    public UserProfileDto getUserProfile(String email) {
        User user = findByEmailWithFriends(email);
        return new UserProfileDto(user.getId(), user.getUsername(), user.getEmail(), user.getMention());
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = findByEmailWithFriends(email);
    
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
    
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            new ArrayList<>()
        );
    }

    public void registerPushToken(String email, String expoPushToken) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setExpoPushToken(expoPushToken);
            userRepository.save(user);
        }
    }
}