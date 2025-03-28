package com.example.chatapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;


import com.example.chatapp.security.JwtTokenUtil;
import com.example.chatapp.user.service.UserService;

import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthenticationInterceptor webSocketAuthInterceptor;

    @Autowired
    public WebSocketConfig(WebSocketAuthenticationInterceptor webSocketAuthInterceptor) {
        this.webSocketAuthInterceptor = webSocketAuthInterceptor;
    }

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userDetailsService;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 클라이언트에게 메시지를 전달할 때의 prefix
        config.enableSimpleBroker("/topic");
        // 서버에 메시지를 보낼 때의 prefix
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket 연결 엔드포인트
//        registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS();
        registry.addEndpoint("/ws")
                .setAllowedOrigins("https://chatapp-server-g6an.onrender.com")
                .addInterceptors(webSocketAuthInterceptor)
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompInterceptor());
    }

    @Bean
    public ChannelInterceptor stompInterceptor() {
        return new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    Object rawHeaders = accessor.getHeader("nativeHeaders");

                    if (rawHeaders instanceof Map<?, ?> headers) {
                        Object authHeader = headers.get("Authorization");

                        if (authHeader instanceof List<?> authHeaderList && !authHeaderList.isEmpty()) {
                            String token = authHeaderList.get(0).toString();

                            if (token.startsWith("Bearer ")) {
                                token = token.substring(7);
                                String email = jwtTokenUtil.getUsername(token);

                                if (email != null) {
                                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                                    if (jwtTokenUtil.validateToken(token, userDetails)) {
                                        Authentication authentication = new UsernamePasswordAuthenticationToken(
                                                userDetails, null, userDetails.getAuthorities());
                                        accessor.setUser(authentication);
                                    }
                                }
                            }
                        }
                    }
                }
                return message;
            }
        };
    }

}
