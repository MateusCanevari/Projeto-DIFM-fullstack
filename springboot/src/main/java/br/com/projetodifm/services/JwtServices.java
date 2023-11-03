package br.com.projetodifm.services;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import br.com.projetodifm.data.vo.v1.security.TokenVO;
import br.com.projetodifm.exceptions.InvalidJwtAuthenticationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtServices {

    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

    @Value("${security.jwt.token.expire-length}")
    private long expireLength;

    public String extractEmail(String token) {
        return extractClaim(Claims::getSubject, token);
    }

    public TokenVO refreshToken(Map<String, Object> extraClaims, String refreshToken, UserDetails userDetails) {
        if (refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring("Bearer ".length());
        }

        if (!isTokenValid(refreshToken, userDetails)) {
            throw new InvalidJwtAuthenticationException();
        }

        return createToken(extraClaims, userDetails);
    }

    public TokenVO refreshToken(String refreshToken, UserDetails userDetails) {
        return refreshToken(new HashMap<>(), refreshToken, userDetails);
    }

    public TokenVO createToken(UserDetails userDetails) {
        return createToken(new HashMap<>(), userDetails);
    }

    public TokenVO createToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return TokenVO.builder()
                .email(userDetails.getUsername())
                .authenticated(true)
                .created(new Date())
                .expiration(new Date(System.currentTimeMillis() + (expireLength * 3)))
                .accessToken(generateToken(extraClaims, userDetails))
                .refreshToken(generateRefreshToken(extraClaims, userDetails))
                .build();
    }

    public Boolean isTokenValid(String token, UserDetails userDetails) {
        var email = extractEmail(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpirated(token));
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (expireLength * 3)))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (expireLength * 6)))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Boolean isTokenExpirated(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(Claims::getExpiration, token);
    }

    private <T> T extractClaim(Function<Claims, T> claimsResolver, String token) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
