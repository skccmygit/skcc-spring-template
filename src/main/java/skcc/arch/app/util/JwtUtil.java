package skcc.arch.app.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import skcc.arch.app.exception.CustomException;
import skcc.arch.app.exception.ErrorCode;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    private final long expirationMillis;    //만료시간(ms)
    private final Key signingKey;

    public JwtUtil(@Value("${jwt.secret}") String secretKey,
                   @Value("${jwt.expiration}") long expirationMillis) {
        this.expirationMillis = expirationMillis;
        this.signingKey = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // 토큰 생성
    public String generateToken(Map<String, Object> claim) {
        return Jwts.builder()
                .setSubject((String) claim.get("uid"))
                .addClaims(claim)
                .setIssuedAt(new Date()) // 발급 시간
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis)) // 만료 시간
                .signWith(signingKey, SignatureAlgorithm.HS256) // 비밀키와 알고리즘으로 서명
                .compact();
    }

    // 토큰 검증 (서명 & 만료 시간 검사)
    public String validateTokenAndExtractUID(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.JWT_EXPIRED_TOKEN);
        } catch (SignatureException e) {
            throw new CustomException(ErrorCode.JWT_INVALID_SIGNATURE);
        } catch (JwtException e) {
            throw new CustomException(ErrorCode.JWT_INVALID);
        }
    }

}