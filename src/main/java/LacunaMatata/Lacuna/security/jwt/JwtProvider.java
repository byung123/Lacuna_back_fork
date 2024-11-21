package LacunaMatata.Lacuna.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/************************************
 * version: 1.0.0                   *
 * author: 손경태                    *
 * description: JwtProvider         *
 * createDate: 2024-10-08           *
 * updateDate: 2024-10-08           *
 ***********************************/
@Component
public class JwtProvider {

    private final Key key;

    public JwtProvider(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    // 인증받은 사용자 토큰 만료 시간
    private Date authorizdExpriedDate() {
        Date authorizedExpiredDate = new Date(new Date().getTime() + (Long) 1000L * 60 * 60 * 24 * 30);
//        Date authorizedExpiredDate = new Date(new Date().getTime() + (Long) 1000L * 60 * 30); // 30분 유효기간
        return authorizedExpiredDate;
    }

//    // 인증받지 않은 사용자 토큰 만료 시간 // 이거 왜 만들었지?
//    private Date notAuthorizdExpriedDate() {
//        Date notAuthorizedExpiredDate = new Date(new Date().getTime() + (Long) 1000L * 60 * 5);
//        return notAuthorizedExpiredDate;
//    }

    // accessToken - bearer 지우기
    public String removeBearer(String bearerToken) {
        if(bearerToken == null) {
            throw new RuntimeException();
        }
        return bearerToken.substring("Bearer ".length());
    }

    // accessToken 생성
    public String generateAccessToken(int userId, String roleName) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("roleName", roleName)
                .expiration(authorizdExpriedDate())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 이메일 인증용 토큰 생성
    public String generateEmailValidToken(String toEmail) {
        Date emailExpireDate = new Date(new Date().getTime() + (1000L * 60 * 5));
        return Jwts.builder()
                .claim("toEmail", toEmail)
                .expiration(emailExpireDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰 값에 있는 클레임 꺼내기
    public Claims getClaim(String token) {
        JwtParser jwtParser = Jwts.parser()
                .setSigningKey(key)
                .build();

        return jwtParser.parseClaimsJws(token).getPayload();
    }
}
