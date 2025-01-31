package com.magroun.server.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

  private final JwtAuthenticationFilter jwtAuthFilter;
  private final AuthenticationProvider authenticationProvider;
  private final LogoutHandler logoutHandler;
  
  public SecurityConfiguration(JwtAuthenticationFilter jwtAuthFilter, AuthenticationProvider authenticationProvider, LogoutHandler logoutHandler) {
      this.jwtAuthFilter = jwtAuthFilter;
      this.authenticationProvider = authenticationProvider;
      this.logoutHandler = logoutHandler;
  }
  
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
      http
              .csrf(AbstractHttpConfigurer::disable)
              .authorizeHttpRequests(req ->
                      req.requestMatchers("/api/v1/auth/**")
                              .permitAll()
                              .anyRequest()
                              .authenticated()
              )
              .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
              .authenticationProvider(authenticationProvider)
              .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
              .logout(logout ->
                      logout.logoutUrl("/api/v1/auth/logout")
                              .addLogoutHandler(logoutHandler)
                              .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
              )
      ;

      return http.build();
  }
}
