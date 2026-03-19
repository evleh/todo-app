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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @SpringBootTest loads the full application context — every bean, every filter,
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

    // ObjectMapper is Jackson's main class for serializing Java objects → JSON.
    // Spring Boot auto-configures one as a bean.
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String USERNAME = "auth-test-user";
    private static final String PASSWORD = "test-password";

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

        // No Authorization header → filter skips → Spring Security sees an
        // anonymous user. One would an anonymous user to be unauthorized (401),
        // but because no AuthenticationEntryPoint is configured explicitly,
        // Spring falls back to Http403ForbiddenEntryPoint. Expect 403, not 401.
        @Test
        void noToken_returns403OnProtectedEndpoint() throws Exception {
            // act & assert
            mockMvc.perform(get("/users"))
                        .andExpect(status().isForbidden());
        }

        @Test
        void validToken_grantsAccessToProtectedEndpoint() throws Exception {
            // arrange: perform login and extract token
            TokenRequest loginRequest = new TokenRequest(USERNAME, PASSWORD);

            String responseJson = mockMvc.perform(post("/auth/token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            String token = objectMapper.readTree(responseJson)
                    .get("accessToken")
                    .asText();

            // act and assert
            mockMvc.perform(get("/users")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk());
        }

        @Test
        void tamperedToken_returns403OnProtectedEndpoint() throws Exception {
            // act & assert
            mockMvc.perform(get("/users").header("Authorization", "Bearer this.is.not.a.valid.jwt"))
                    .andExpect(status().isForbidden());
        }
    }
}
