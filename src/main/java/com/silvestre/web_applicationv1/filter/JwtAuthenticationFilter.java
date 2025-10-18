package com.silvestre.web_applicationv1.filter;

import com.silvestre.web_applicationv1.service.MyUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    MyUserDetailsService userDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        String fullPath= request.getRequestURI();
        System.out.println("fullPath = " + fullPath);

        System.out.println("Path in shouldNotFilter: " + path);

        boolean shouldSkip = path.equals("/login")
                || path.equals("/api/featured-services")
                || path.startsWith("/api/featured-services/")
                || path.equals("/register")
                || path.startsWith("/public")
                || path.equals("/upload")
                || path.equals("/paymongo/webhook")
                || path.startsWith("/paymongo")
                || path.startsWith("/upload/")
                || path.startsWith("/h2-console");

        System.out.println("shouldSkip: " + shouldSkip);
        return shouldSkip;
          }
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String jwt;
        final String username;

        jwt= extractJwtFromCookies(request);
        if (jwt == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid JWT token");
            return; // 🔥 Stop here — no token, no access
        }

        try {
            username = jwtUtil.extractUsername(jwt);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    List<GrantedAuthority> currentUserRole = jwtUtil.extractRoleFromToken(jwt);

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, currentUserRole, currentUserRole);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    clearJwtCookie(response);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Invalid JWT Token");
                    return;
                }
            }
        } catch (ExpiredJwtException e) {
            clearJwtCookie(response);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("JWT Token expired");
            return;
        } catch (Exception e) {
            clearJwtCookie(response);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Authentication failed: " + e.getMessage());
            return;
        }




        filterChain.doFilter(request, response);

    }


    private String extractJwtFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
    private void clearJwtCookie(HttpServletResponse response) {
        ResponseCookie deleteCookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false) // ✅ true in prod with HTTPS
                .path("/")
                .maxAge(0)     // expire immediately
                .sameSite("Lax")
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
    }
}
