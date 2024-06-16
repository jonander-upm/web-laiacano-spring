package com.laiacano.core.config.jwt;

import java.util.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import com.laiacano.core.data.entities.Role;
import com.laiacano.core.data.exceptions.UnauthorizedException;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenProvider {
    private static final String USERNAME_CLAIM = "username";
    private static final String EMAIL_CLAIM = "email";
    private static final String ROLE_CLAIM = "role";

    private static final String ROLE_PREFIX = "ROLE_";

    @Value("${app.jwt.secret}")
    private String secretKey;
    @Value("${app.jwt.validity}")
    private long validityInMilliseconds = 3600000;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String username, String email, Role role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(USERNAME_CLAIM, username);
        claims.put(EMAIL_CLAIM, email);
        claims.put(ROLE_CLAIM, role);
        return Jwts.builder()
                .addClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(generateValidityDate())
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String refreshToken(String token) {
        if(!isTokenValid(token)) {
            return null;
        }
        return createToken(getUsername(token), getEmail(token), getRole(token));
    }

    private Date generateValidityDate() {
        Date now = new Date();
        return new Date(now.getTime() + validityInMilliseconds);
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        String username = claims.get(USERNAME_CLAIM, String.class);
        GrantedAuthority authority = new SimpleGrantedAuthority(ROLE_PREFIX + claims.get(ROLE_CLAIM));
        User principal = new User(username, "", List.of(authority));
        return new UsernamePasswordAuthenticationToken(principal,
                token,  List.of(authority));
    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get(USERNAME_CLAIM, String.class);
    }

    public String getEmail(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get(EMAIL_CLAIM, String.class);
    }

    public Role getRole(String token) {
        String role =  Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get(ROLE_CLAIM, String.class);
        return Role.valueOf(role);
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        } catch (Exception e) {
            throw new UnauthorizedException("Not a valid token for user: " + this.getUsername(token));
        }
        return true;
    }

}
