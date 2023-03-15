package ru.netology.netologydiploma.security.customization;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
@Slf4j
public class JWTProvider {
//    @Value("$(secret_key)")
    private String jwtSecret= "secret";
    //TODO как вынести значение переменной типа Long в properties
    //@Value("$(timeLiveToken)")
    private Long timeLiveToken = 300L;

    public String generateToken(String login) {
        //ОПРЕДЕЛЕНИЯ ВРЕМЕНИЯ ЖИЗНИ ТОКЕНА
        final LocalDateTime now = LocalDateTime.now();
        final Instant accessExpirationInstant = now.plusMinutes(timeLiveToken).atZone(ZoneId.systemDefault()).toInstant();
        final Date accessExpiration = Date.from(accessExpirationInstant);
        return Jwts.builder()
                .setSubject(login)
                .setExpiration(accessExpiration)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getLoginFromToken(String token) {
        Claims claims = Jwts
                .parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

}
