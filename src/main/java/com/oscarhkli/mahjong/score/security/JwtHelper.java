package com.oscarhkli.mahjong.score.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class JwtHelper {

  private static final long MINUTE_TO_MILLISECOND = 60 * 1000L;

  private final String secretKey;
  private final long tokenExpirationTime;
  private final long refreshTokenExpirationTime;

  public JwtHelper(
      @Value("${auth.secret-key}") String secretKey,
      @Value("${auth.token-expiration-minute}") long tokenExpirationMinute,
      @Value("${auth.refresh-token-expiration-minute}") long refreshTokenExpirationMinute
  ) {
    this.secretKey = secretKey;
    this.tokenExpirationTime = tokenExpirationMinute * MINUTE_TO_MILLISECOND;
    this.refreshTokenExpirationTime = refreshTokenExpirationMinute * MINUTE_TO_MILLISECOND;
  }

  // Use Keys.hmacShaKeyFor to create a secure key
  private SecretKey getKey() {
    var keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String generateToken(String subject) {
    return Jwts.builder()
        .subject(subject)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + tokenExpirationTime))
        .signWith(getKey())
        .compact();
  }

  public String generateToken(String subject, List<String> roles) {
    return Jwts.builder()
        .subject(subject)
        .claim("roles", roles)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + tokenExpirationTime))
        .signWith(getKey())
        .compact();
  }

  // Generate Refresh Token
  public String generateRefreshToken(String username) {
    return Jwts.builder()
        .subject(username)
        .expiration(new Date(System.currentTimeMillis() + refreshTokenExpirationTime))
        .signWith(getKey())
        .compact();
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
    return claimResolver.apply(extractAllClaims(token));
  }

  public String extractSubject(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
  }

  public boolean validateToken(String token, String subject) {
    var tokenSubject = extractSubject(token);
    return subject.equals(tokenSubject) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public List<SimpleGrantedAuthority> extractAuthorities(String token) {
    return extractRoles(token).stream().map(SimpleGrantedAuthority::new).toList();
  }

  @SuppressWarnings("unchecked")
  public List<String> extractRoles(String token) {
    return Optional.ofNullable(extractAllClaims(token).get("roles", List.class))
        .orElseGet(List::of);
  }
}
