package com.auth.Authentication.config;


import com.auth.Authentication.Entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;



@Component
public class JWTGenerator {

    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    public String generateToken(Authentication authentication){
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expireDate= new Date(currentDate.getTime() + com.auth.Authentication.config.SecurityConstants.JWT_EXPIRATION);
        String token= Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, com.auth.Authentication.config.SecurityConstants.JWT_SECRET)
                .compact();
        return token;
    }

    public  String createToken(String username){
        Date currentDate = new Date();
        Date expireDate= new Date(currentDate.getTime() + com.auth.Authentication.config.SecurityConstants.JWT_EXPIRATION);
        String tokenOtp= Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, com.auth.Authentication.config.SecurityConstants.JWT_SECRET)
                .compact();
        return tokenOtp;
    }

    public String getUsernameFromJWT(String token){
        Claims claims= Jwts.parser()
                .setSigningKey(SecurityConstants.JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token){
        try{
            Jwts.parser().setSigningKey(SecurityConstants.JWT_SECRET).parseClaimsJws(token);
            return true;
        }catch (Exception ex){
            throw new AuthenticationCredentialsNotFoundException("JWT was expired or incorrect");
        }
    }

}
