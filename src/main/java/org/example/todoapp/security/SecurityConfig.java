package org.example.todoapp.security;

import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
//public class SecurityConfig {
//
//    // Searvice ist bestimmte art von bean , wird durch bean annotation erstellt
//    @Bean
//    public void filterChain(HttpSecurity http) throws Exception {
//        // CORS: cross origin ressource sharing. back und frontend  sollen miteinander ressourcen teilen, per devault darf man das nicht.
//        //CSRF: requests abssenden über javascript versteckt über andere seite. seite nimmt aus browser meine daten die im browser gespeichert sind und verwendete sie unabhängig von dem was ich mag
////        http.cors().and().csrf().disable();
////        // state management
////        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // sessionmanagment ist stateless i.e. uns interessiert nur der aktuelle request
////        http.formLogin().disable(); // um login dialog der per default da ist weg zu bekommen
//
//
//        // man darf sich registrieren und login machen , alle anderen anfragen müssen authorisiert sein
//        // swagger ui muss man auch hinzufügen sonst sieht man gar nichts mehr
////        http.authorizeHttpRequests(registry -> registry.requestMatchers("auth/resister").permitAll().
////                requestMatchers("auth/resister").permitAll())
//
//
//    }
//
//}
