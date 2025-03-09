package com.example.chatapp.user.dto;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import com.example.chatapp.user.entity.User;

@Data
public class UserDto {

    private Long id;

    @NotEmpty(message = "Username is required")
    private String username;

    @NotEmpty(message = "Password is required")
    private String password;

    @Email(message = "Email should be valid")
    @NotEmpty(message = "Email is required")
    private String email;

    private String mention;

    public static UserDto fromEntity(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setMention(user.getMention());
        return dto;
    }
}
