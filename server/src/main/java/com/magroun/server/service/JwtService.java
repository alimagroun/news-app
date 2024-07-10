package com.magroun.server.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletResponse;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.magroun.server.model.Token;
import com.magroun.server.model.TokenType;
import com.magroun.server.model.User;
import com.magroun.server.repository.TokenRepository;
import com.magroun.server.repository.UserRepository;

@Service
public class JwtService {

  @Value("${application.security.jwt.secret-key}")
  private String secretKey;
  @Value("${application.security.jwt.expiration}")
  private long jwtExpiration;
  @Value("${application.security.jwt.refresh-token.expiration}")
  private long refreshExpiration;
  
  private final UserRepository repository;
  private final TokenRepository tokenRepository;
  
  public JwtService(UserRepository repository, TokenRepository tokenRepository) {
      this.repository = repository;
      this.tokenRepository = tokenRepository;
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public String generateToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  public String generateToken(
      Map<String, Object> extraClaims,
      UserDetails userDetails
  ) {
    return buildToken(extraClaims, userDetails, jwtExpiration);
  }

  public String generateRefreshToken(
      UserDetails userDetails
  ) {
    return buildToken(new HashMap<>(), userDetails, refreshExpiration);
  }

  private String buildToken(
          Map<String, Object> extraClaims,
          UserDetails userDetails,
          long expiration
  ) {
    return Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  public  boolean isTokenExpired(String token) {
      try {
          return extractExpiration(token).before(new Date());
      } catch (ExpiredJwtException expiredJwtException) {
          return true;
      }
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
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
  
  
  public String refreshToken(HttpServletResponse response, String refreshToken) {
	    final String userEmail = extractUsername(refreshToken);
	    
	    if (userEmail != null) {
	        var user = this.repository.findByEmail(userEmail)
	                .orElseThrow();

	        if (isTokenValid(refreshToken, user)) {
	            var accessToken = generateToken(user);
		        saveUserToken(user, accessToken, TokenType.ACCESS);

	            ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", accessToken)
	                    .httpOnly(true)
	                    .secure(true)
	                    .path("/api")
	                    .maxAge(24 * 60 * 60)
	                    .build();
	            response.addHeader("Set-Cookie", accessTokenCookie.toString());
	            return accessToken;
	        }
	    }
	    
	    return ""; 
	}

  private void saveUserToken(User user, String jwtToken, TokenType tokenType) {
	    Token token = new Token();
	    token.setUser(user);
	    token.setToken(jwtToken);
	    token.setTokenType(tokenType);
	    token.setExpired(false);
	    token.setRevoked(false);
	    tokenRepository.save(token);
	}  
}
