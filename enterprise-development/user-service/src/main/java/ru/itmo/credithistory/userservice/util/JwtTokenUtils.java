package ru.itmo.credithistory.userservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;
import ru.itmo.credithistory.userservice.configuration.JwtConfigurationProperties;

@Component
@RequiredArgsConstructor
@NullMarked
public class JwtTokenUtils {

  private final JwtConfigurationProperties jwtProperties;

  public String generateToken(UUID userId) {
    Date issuedDate = new Date();
    Date expiredDate = new Date(issuedDate.getTime() + jwtProperties.getLifetime().toMillis());
    return Jwts.builder()
        .subject(userId.toString())
        .issuedAt(issuedDate)
        .expiration(expiredDate)
        .signWith(getSecretKey())
        .compact();
  }

  public UUID getUserIdFromToken(String token) {
    char[] userId = getAllClaimsFromToken(token).getSubject().toCharArray();
    return UUID.fromString(new String(userId));
  }

  private Claims getAllClaimsFromToken(String token) {
    return Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token).getPayload();
  }

  private SecretKey getSecretKey() {
    return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
  }
}
