package com.magroun.server.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseCookie;

import com.magroun.server.dto.AuthenticationRequest;
import com.magroun.server.dto.RegisterRequest;
import com.magroun.server.service.AuthenticationService;
import org.springframework.security.core.Authentication;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

  private final AuthenticationService service;
  
  public AuthenticationController(AuthenticationService service) {
      this.service = service;
  }

  @PostMapping("/register")
  public ResponseEntity<Void> register(
      @RequestBody RegisterRequest request,
      HttpServletResponse response
  ) {
      ResponseCookie accessTokenCookie = service.register(request, response);
      return ResponseEntity.ok().build();
  }
  
  @PostMapping("/authenticate")
  public ResponseEntity<Void> authenticate(
      @RequestBody AuthenticationRequest request,
      HttpServletResponse response
  ) {
      service.authenticate(request, response);
      return ResponseEntity.ok().build();
  }

  @PostMapping("/refresh-token")
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    service.refreshToken(request, response);
  }

  @GetMapping("/is-logged-in")
  public ResponseEntity<Boolean> isLoggedIn(Authentication authentication) {
      boolean isAuthenticated = authentication != null && authentication.isAuthenticated();
      return ResponseEntity.ok(isAuthenticated);
  }
}
