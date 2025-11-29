package com.silvestre.web_applicationv1.config;

import com.silvestre.web_applicationv1.filter.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired
    private JwtUtil jwtUtil;


    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {


        System.out.println(">>> Handshake intercepted");
        if (request instanceof ServletServerHttpRequest servletRequest) {
            var httpRequest = servletRequest.getServletRequest(); // this is HttpServletRequest

            var cookies = httpRequest.getCookies();
            if (cookies != null) {
                for (var cookie : cookies) {
                    if ("jwt".equals(cookie.getName())) {
                        String token = cookie.getValue();

                        try {
                            String username = jwtUtil.extractUsername(token); // extract from JWT
                            System.out.println("Username from JWT: " + username); // log for debugging
                            System.out.println(">>> WebSocket session username: " + username);
                            attributes.put("userPrincipal", new StompPrincipal(username));
                        } catch (Exception e) {
                            System.out.println("Failed to extract username from JWT: " + e.getMessage());
                        }
                        break;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class StompPrincipal implements java.security.Principal {
        private final String name;

    }
}
