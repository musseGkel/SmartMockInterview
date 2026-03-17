package com.smartmock.interview.auth.infrastructure;

import com.smartmock.interview.auth.domain.UserAccount;
import com.smartmock.interview.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private static final String CLAIM_TYPE = "type";
    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_ROLE = "role";

    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";

    private final JwtProperties jwtProperties;
    private final SecretKey signingKey;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.signingKey = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(UserAccount user) {
        Instant now = Instant.now();
        Instant expiry = now.plus(jwtProperties.accessTtlMinutes(), ChronoUnit.MINUTES);

        return Jwts.builder()
                .issuer(jwtProperties.issuer())
                .subject(user.getId())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .claims(Map.of(
                        CLAIM_TYPE, TOKEN_TYPE_ACCESS,
                        CLAIM_EMAIL, user.getEmail(),
                        CLAIM_ROLE, user.getRole()))
                .signWith(signingKey)
                .compact();
    }

    public String generateRefreshToken(UserAccount user) {
        Instant now = Instant.now();
        Instant expiry = now.plus(jwtProperties.refreshTtlDays(), ChronoUnit.DAYS);

        return Jwts.builder()
                .issuer(jwtProperties.issuer())
                .subject(user.getId())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .claims(Map.of(
                        CLAIM_TYPE, TOKEN_TYPE_REFRESH))
                .signWith(signingKey)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .requireIssuer(jwtProperties.issuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isValid(String token) {
        try {
            Claims claims = parse(token);
            return claims.getExpiration().after(new Date());
        } catch (ExpiredJwtException ex) {
            return false;
        } catch (UnsupportedJwtException ex) {
            return false;
        } catch (MalformedJwtException ex) {
            return false;
        } catch (SecurityException ex) {
            return false;
        } catch (IllegalArgumentException ex) {
            return false;
        } catch (JwtException ex) {
            return false;
        }
    }

    public boolean isAccessTokenValid(String token) {
        if (!isValid(token)) {
            return false;
        }
        return TOKEN_TYPE_ACCESS.equals(extractType(token));
    }

    public boolean isRefreshTokenValid(String token) {
        if (!isValid(token)) {
            return false;
        }
        return TOKEN_TYPE_REFRESH.equals(extractType(token));
    }

    public String extractUserId(String token) {
        return parse(token).getSubject();
    }

    public String extractEmail(String token) {
        return parse(token).get(CLAIM_EMAIL, String.class);
    }

    public String extractRole(String token) {
        return parse(token).get(CLAIM_ROLE, String.class);
    }

    public String extractType(String token) {
        return parse(token).get(CLAIM_TYPE, String.class);
    }

    public Instant extractExpiration(String token) {
        return parse(token).getExpiration().toInstant();
    }

    public long getAccessTokenExpiresInSeconds() {
        return jwtProperties.accessTtlMinutes() * 60;
    }
}