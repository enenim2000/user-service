package com.elara.userservice.util;

import com.elara.userservice.enums.ResponseCode;
import com.elara.userservice.exception.AppException;
import com.elara.userservice.service.MessageService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JWTTokens {

    @Value("${oauth.jwt.secret}")
    private String jwtSecret;

    private final MessageService messageService;

    public JWTTokens(MessageService messageService) {
        this.messageService = messageService;
    }

    public String createJWT(String id, String issuer, String subject, long ttlMillis) {

        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //We will sign our JWT with our ApiKey secret
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));


        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder().setId(id)
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(issuer)
                .signWith(key, signatureAlgorithm);

        //if it has been specified, let's add the expiration
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

    public Claims parseJWT(String jwtToken) {
        try {
            Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

            // Get Claims from valid token
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwtToken).getBody();
        } catch (ExpiredJwtException e) {
            log.info("Token expired: {}", e.getClaims());
            throw new AppException(messageService.getMessage("Token.Expired"));
            // Get Claims from expired token
            //return e.getClaims();
        }
    }
}
