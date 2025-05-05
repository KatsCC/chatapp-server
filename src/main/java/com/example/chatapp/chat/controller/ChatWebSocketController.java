package com.example.chatapp.chat.controller;

import com.example.chatapp.chat.dto.ChatMessageDto;
import com.example.chatapp.chat.entity.ChatMessage;
import com.example.chatapp.chat.service.ChatMessageService;
import com.example.chatapp.user.entity.User;
import com.example.chatapp.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatWebSocketController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final UserService userService;



    @Autowired
    public ChatWebSocketController(SimpMessageSendingOperations messagingTemplate,
                                   ChatMessageService chatMessageService,
                                   UserService userService) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessageService = chatMessageService;
        this.userService = userService;
    }

    @MessageMapping("/chat/rooms/{chatRoomId}/message")
    public void sendMessage(@DestinationVariable Long chatRoomId,
                            @Payload String content,
                            Principal principal) {

        String email = principal.getName();
        User sender = userService.findByEmail(email);

        ChatMessage message = chatMessageService.saveMessage(chatRoomId, sender, content);

        ChatMessageDto messageDto = ChatMessageDto.fromEntity(message);

        messagingTemplate.convertAndSend("/topic/chat/rooms/" + chatRoomId, messageDto);
    }
}
