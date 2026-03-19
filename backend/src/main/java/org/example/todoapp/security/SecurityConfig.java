package org.example.todoapp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Definition of @Configuration with a SecurityFilterChain switches off the default webapp security settings in Spring Boot.
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final TokenAuthenticationFilter tokenAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(TokenAuthenticationFilter tokenAuthenticationFilter, CustomUserDetailsService customUserDetailsService) {
        this.tokenAuthenticationFilter = tokenAuthenticationFilter;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        // CORS: disabled — frontend and backend are served from the same origin in this project.
        http.cors(AbstractHttpConfigurer::disable);
        // CSRF: disabled — not needed for stateless JWT auth (no session cookies to exploit).
        http.csrf(AbstractHttpConfigurer::disable);
        // Stateless session: each request is authenticated independently via the JWT — no server-side session.
        http.sessionManagement(conf -> conf.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.formLogin(AbstractHttpConfigurer::disable);
        // Public routes: registration, login, and API docs. Everything else requires a valid JWT.
        http.authorizeHttpRequests(
                registry -> registry
                        .requestMatchers(HttpMethod.POST, "/users").permitAll()
                        .requestMatchers("/auth/token/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger.html").permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().authenticated()
        );

        return http.build();

    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(passwordEncoder);
        provider.setUserDetailsService(customUserDetailsService);

        return new ProviderManager(provider);
    }



}

