package com.magroun.server.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.magroun.server.repository.TokenRepository;
import com.magroun.server.service.JwtService;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;
  private final TokenRepository tokenRepository;
  
  public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService, TokenRepository tokenRepository) {
      this.jwtService = jwtService;
      this.userDetailsService = userDetailsService;
      this.tokenRepository = tokenRepository;
  }

  @Override
  protected void doFilterInternal(
          @NonNull HttpServletRequest request,
          @NonNull HttpServletResponse response,
          @NonNull FilterChain filterChain
  ) throws ServletException, IOException {
      String jwt = null;
      String refreshToken = null;

      Cookie[] cookies = request.getCookies();
      if (cookies != null) {
          for (Cookie cookie : cookies) {
              if ("access_token".equals(cookie.getName())) {
                  jwt = cookie.getValue();
              } else if ("refresh_token".equals(cookie.getName())) {
                  refreshToken = cookie.getValue();
              }
              
              if (jwt != null && refreshToken != null) {
                  break;
              }
          }
      }

      if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
          
          if(jwtService.isTokenExpired(jwt)&&!jwtService.isTokenExpired(refreshToken)) {
        	jwt=  jwtService.refreshToken(response, refreshToken);   	
          }
          String userEmail = jwtService.extractUsername(jwt);
          UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
          boolean isTokenValid = tokenRepository.findByToken(jwt)
                  .map(t -> !t.isExpired() && !t.isRevoked())
                  .orElse(false);
                    
          if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
              UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                      userDetails,
                      null,
                      userDetails.getAuthorities()
              );
              authToken.setDetails(
                      new WebAuthenticationDetailsSource().buildDetails(request)
              );
              SecurityContextHolder.getContext().setAuthentication(authToken);
          }
      }
      filterChain.doFilter(request, response);
  }
}
