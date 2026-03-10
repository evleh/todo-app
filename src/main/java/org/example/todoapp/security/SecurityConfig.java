package org.example.todoapp.security;

import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

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
        //CORS: cross origin ressource sharing. back und frontend  sollen miteinander ressourcen teilen, per devault darf man das nicht.
        http.cors(AbstractHttpConfigurer::disable);
        //CSRF: requests abssenden über javascript versteckt über andere seite. seite nimmt aus browser meine daten die im browser gespeichert sind und verwendete sie unabhängig von dem was ich mag
        http.csrf(AbstractHttpConfigurer::disable);
        // state management: sessionmanagment ist stateless i.e. uns interessiert nur der aktuelle request
        http.sessionManagement(conf -> conf.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.formLogin(AbstractHttpConfigurer::disable); // um login dialog der per default da ist weg zu bekommen
        // man darf sich registrieren und login machen , alle anderen anfragen müssen authorisiert sein
        // swagger ui muss man auch hinzufügen sonst sieht man gar nichts mehr
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

