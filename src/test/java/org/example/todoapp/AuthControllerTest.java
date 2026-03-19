package org.example.todoapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.todoapp.dto.TokenRequest;
import org.example.todoapp.entity.MyUser;
import org.example.todoapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// [JUNIOR] @SpringBootTest loads the full application context — every bean, every filter,
// every repository. Requests travel through Spring Security, the controller, the service,
// and all the way to the real database, making this a true integration test.
//
// @AutoConfigureMockMvc wires up MockMvc so we can send HTTP requests without
// actually starting a server on a port.
//
// ⚠️  Requires a running PostgreSQL instance (docker compose up).
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // [JUNIOR] ObjectMapper is Jackson's main class for serializing Java objects → JSON.
    // Spring Boot auto-configures one as a bean.
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String USERNAME = "auth-test-user";
    private static final String PASSWORD = "test-password";

    // [JUNIOR] @BeforeEach runs before every @Test method.
    // We wipe and recreate the test user so each test starts from a known, clean state —
    // preventing one test's side effects from breaking another.
    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        MyUser user = new MyUser();
        user.setUsername(USERNAME);
        user.setPassword(passwordEncoder.encode(PASSWORD));
        userRepository.save(user);
    }

    @Nested
    class LoginTests {

        @Test
        void validCredentials_returns200WithToken() throws Exception {
            // arrange
            TokenRequest request = new TokenRequest(USERNAME, PASSWORD);

            // act & assert
            mockMvc.perform(post("/auth/token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    // [JUNIOR] jsonPath lets you assert on specific fields in a JSON response.
                    // "$.accessToken" means: top-level field named "accessToken".
                    // exists() checks the field is present (and not null).
                    .andExpect(jsonPath("$.accessToken").exists());
        }

        @Test
        void wrongPassword_returns403() throws Exception {
            // arrange
            TokenRequest request = new TokenRequest(USERNAME, "test-wrong-password");

            // act & assert
            mockMvc.perform(post("/auth/token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }

        @Test
        void unknownUsername_returns403() throws Exception {
            // arrange
            TokenRequest request = new TokenRequest("test-wrong-username", PASSWORD);

            // act & assert
            mockMvc.perform(post("/auth/token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }

        // [JUNIOR] Hint: send {"username": "", "password": ""} and expect 400.
        // This exercises @NotBlank on TokenRequest — validation fires before the method runs.
        @Test
        void blankFields_returns400() throws Exception {
            // arrange
            TokenRequest request = new TokenRequest("", "");

            // act & assert
            mockMvc.perform(post("/auth/token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class TokenVerificationTests {

        // [MID] Hint: no Authorization header → filter skips → Spring Security sees an
        // anonymous user. Because no AuthenticationEntryPoint is configured explicitly,
        // Spring falls back to Http403ForbiddenEntryPoint. Expect 403, not 401.
        @Test
        void noToken_returns403OnProtectedEndpoint() throws Exception {
            // TODO
        }

        @Test
        void validToken_grantsAccessToProtectedEndpoint() throws Exception {
            // TODO
        }

        // [MID] Hint: pass "Bearer this.is.not.a.valid.jwt" — the filter catches the
        // parse exception, skips authentication, and the result is the same as no token.
        @Test
        void tamperedToken_returns403OnProtectedEndpoint() throws Exception {
            // TODO
        }
    }
}
