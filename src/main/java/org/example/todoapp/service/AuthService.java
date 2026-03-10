package org.example.todoapp.service;

import org.example.todoapp.dto.Token;
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

    public Token createToken(TokenRequest tokenRequest) {
        // Authentication Object
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(tokenRequest.getUsername(), tokenRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Token token = new Token();
        token.setAccessToken(userPrincipal.getUserId()); // hier jwt token setzen!!!!

        return token;
    }

}
