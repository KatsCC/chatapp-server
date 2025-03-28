package com.example.chatapp.config;

import com.example.chatapp.security.JwtTokenUtil;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class WebSocketAuthenticationInterceptor implements HandshakeInterceptor {

    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public WebSocketAuthenticationInterceptor(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // 우선 Authorization 헤더에서 JWT 토큰 추출 시도
        String token = null;
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        // 만약 헤더에서 토큰이 없다면, 쿼리 파라미터에서 "token" 파라미터를 추출
        if (token == null) {
            URI uri = request.getURI();
            Map<String, List<String>> queryParams = UriComponentsBuilder.fromUri(uri).build().getQueryParams();
            List<String> tokenList = queryParams.get("token");
            if (tokenList != null && !tokenList.isEmpty()) {
                token = tokenList.get(0);
            }
        }

        // 토큰이 존재하면, JWT 검증을 수행합니다.
        if (token != null) {
            try {
                String username = jwtTokenUtil.getUsername(token);
                if (username != null) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (JwtException | IllegalArgumentException e) {
                System.err.println("Invalid JWT token: " + e.getMessage());
            }
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 후처리 (필요한 경우 구현)
    }
}
