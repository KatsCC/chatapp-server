package com.example.chatapp.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.chatapp.security.JwtTokenUtil;
import com.example.chatapp.user.dto.UserDto;
import com.example.chatapp.user.entity.User;
import com.example.chatapp.user.service.UserService;

@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    // 로그인
    @PostMapping("/login")
    public String createAuthenticationToken(@RequestBody UserDto userDto) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userDto.getEmail(), userDto.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        final UserDetails userDetails = userService
                .loadUserByUsername(userDto.getEmail());

        final String jwt = jwtTokenUtil.createToken(userDetails.getUsername());

        return jwt;
    }

    // 회원가입
    @PostMapping("/registration")
    public String registerUser(@RequestBody UserDto userDto) {
        User existingUser = userService.findByEmail(userDto.getEmail());
        if (existingUser != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        userService.save(userDto);
        return "User registered successfully";
    }
}
