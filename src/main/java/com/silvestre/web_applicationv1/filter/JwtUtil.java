package com.silvestre.web_applicationv1.filter;

import com.silvestre.web_applicationv1.ExceptionHandler.InvalidRoleAccess;
import com.silvestre.web_applicationv1.entity.User;
import com.silvestre.web_applicationv1.enums.Role;
import com.silvestre.web_applicationv1.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {


    private final JwtProperties jwtProperties;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateToken(User user){
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .claim("role",user.getRole().name())
                .setExpiration(new Date(System.currentTimeMillis()+ jwtProperties.getExpirationMs()))
                .signWith(Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token){
        String secretKey= jwtProperties.getSecretKey();
        return Jwts.parserBuilder().setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public List<GrantedAuthority> extractRoleFromToken(String token){
        String secretKey= jwtProperties.getSecretKey();
        Claims claims= Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes()).build().
                parseClaimsJws(token).getBody();

        String role = claims.get("role",String.class);
        System.out.println("role = " + role);
        try{
            Role.valueOf(role);
        }catch (IllegalArgumentException ex){
            throw  new InvalidRoleAccess("invalid role provided");
        }

        return List.of( new SimpleGrantedAuthority("ROLE_"+ role));
    }


    public boolean validateToken(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);

    }

    private boolean isTokenExpired(String token){
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(jwtProperties.getSecretKey().getBytes())
                .build().parseClaimsJws(token)
                .getBody().getExpiration();
        return expiration.before(new Date());
    }
}
