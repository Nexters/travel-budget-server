package com.strictmanager.travelbudget.infra.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

@RequiredArgsConstructor
public class JwtTokenUtil {

    public static final long JWT_ACCESS_TOKEN_VALIDITY_HOURS = 24; // 24hours
    public static final long JWT_REFRESH_TOKEN_VALIDITY_HOURS = 30 * 24; // 30days

    private final String secret;

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails.getUsername(), JWT_ACCESS_TOKEN_VALIDITY_HOURS);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails.getUsername(), JWT_REFRESH_TOKEN_VALIDITY_HOURS);
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(secret)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    private String doGenerateToken(Map<String, Object> claims, String subject, long tokenValidityHour) {
        final long tokenValidityMillis = tokenValidityHour * 60 * 60 * 1000;
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + tokenValidityMillis))
            .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
            .compact();
    }
}
