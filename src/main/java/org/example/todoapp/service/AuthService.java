package org.example.todoapp.service;

import org.example.todoapp.dto.TokenResponse;
import org.example.todoapp.dto.TokenRequest;
import org.example.todoapp.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;



@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;

    public AuthService(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public TokenResponse createToken(TokenRequest tokenRequest) {
        // Authentication Object
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(tokenRequest.username(), tokenRequest.password())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        TokenResponse token = new TokenResponse(userPrincipal.getUserId()); // hier jwt token setzen!!!!

        return token;
    }

}
