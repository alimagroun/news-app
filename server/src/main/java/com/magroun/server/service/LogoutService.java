package com.magroun.server.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import com.magroun.server.repository.TokenRepository;

@Service
public class LogoutService implements LogoutHandler {

  private final TokenRepository tokenRepository;
  
  public LogoutService(TokenRepository tokenRepository) {
      this.tokenRepository = tokenRepository;
  }

  @Override
  public void logout(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) {
	    Cookie[] cookies = request.getCookies();
	 if (cookies != null) {

	     for (Cookie cookie : cookies) {
	         if ("access_token".equals(cookie.getName())) {
	             
	             var storedToken = tokenRepository.findByToken(cookie.getValue())
	            	        .orElse(null);
	             if (storedToken != null) {
	                 storedToken.setExpired(true);
	                 storedToken.setRevoked(true);
	                 tokenRepository.save(storedToken);
	                 SecurityContextHolder.clearContext();
	              
	             Cookie accessTokenCookie = new Cookie("access_token", null);
	             accessTokenCookie.setMaxAge(0);
	             accessTokenCookie.setPath("/api");
	             accessTokenCookie.setHttpOnly(true);
	             response.addCookie(accessTokenCookie);
	         } 
	             }
	         else if ("refresh_token".equals(cookie.getName())) {
	        	 
	             var storedToken = tokenRepository.findByToken(cookie.getValue())
	            	        .orElse(null);
	             if (storedToken != null) {
	                 storedToken.setExpired(true);
	                 storedToken.setRevoked(true);
	                 tokenRepository.save(storedToken);
	                 SecurityContextHolder.clearContext();

                 Cookie refreshTokenCookie = new Cookie("refresh_token", null);
                 refreshTokenCookie.setMaxAge(0);
                 refreshTokenCookie.setPath("/api");
                 refreshTokenCookie.setHttpOnly(true);
                 response.addCookie(refreshTokenCookie);
             }
	     }
	 }
  }
  }
  }
