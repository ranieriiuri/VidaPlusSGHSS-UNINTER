package com.vidaplus.sghss_backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    // 🔑 Chave secreta para assinar os tokens (em produção deve estar em application.properties)
    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Tempo de expiração do token (ex: 24h)
    private final long jwtExpiration = 1000 * 60 * 60 * 24;

    /**
     * Gera um token JWT para o usuário
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date()) // data de emissão
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // expiração
                .signWith(secretKey)
                .compact();
    }

    /**
     * Extrai o username (subject) do token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Valida se o token é válido e não expirou
     */
    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    /**
     * Verifica se o token está expirado
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
