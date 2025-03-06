package community.utill;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret-key}")
    private  String SECRET_KEY;
    @Value("${jwt.ACCESS_TOKEN_EXPIRATION}")
    private  Long ACCESS_TOKEN_EXPIRATION;
    @Value("${jwt.REFRESH_TOKEN_EXPIRATION}")
    private  Long REFRESH_TOKEN_EXPIRATION;
    // JWT 토큰 생성 메서드
    public  String generateAccessToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .compact();
    }
    public  String generateRefreshToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .compact();
    }

    // JWT 토큰에서 userId를 추출하는 메서드 (Bearer 토큰 처리)
    public  String extractUserId(String bearerToken) {
        // Bearer 문자열을 제거하여 토큰 부분만 추출
        String token = bearerToken.substring(7);  // "Bearer " 길이는 7
        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY.getBytes())  // 서명 검증을 위한 비밀 키 설정
                .build();
        Claims claims = parser.parseClaimsJws(token).getBody();  // 서명된 JWT 파싱
        return claims.getSubject();  // JWT의 subject는 userId로 저장됩니다.
    }
}
