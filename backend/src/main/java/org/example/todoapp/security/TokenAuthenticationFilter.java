package org.example.todoapp.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public TokenAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        try {
            UserPrincipal userPrincipal = jwtService.parseToken(token);
            SecurityContextHolder.getContext().setAuthentication(
                    new UserPrincipalAuthenticationToken(userPrincipal)
            );
        } catch (JwtException e) {
            // Invalid or expired token — do not set authentication.
            // Spring Security will reject the request 
            // Request should be rejected with 401 -> requires configuration -> currently rejected with 403 
        }

        filterChain.doFilter(request, response);
    }
}