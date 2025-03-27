package com.example.chatapp.user.dto;

import lombok.Data;
import com.example.chatapp.user.entity.User;

@Data
public class UserSimpleDto {
    private Long id;
    private String username;

    public static UserSimpleDto fromEntity(User user) {
        UserSimpleDto dto = new UserSimpleDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        return dto;
    }
}
