package org.example.todoapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.example.todoapp.dto.TokenRequest;
import org.example.todoapp.dto.UserUpdateRequest;
import org.example.todoapp.entity.MyUser;
import org.example.todoapp.entity.Role;
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

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest 
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String PASSWORD = "test-password";
    private static final String REGULAR_USERNAME = "regular-user";
    private static final String ADMIN_USERNAME = "admin-user";

    private MyUser user;
    private MyUser adminUser; 
    
    @BeforeEach
    void setUp() {

        userRepository.deleteAll();

        user = new MyUser();
        user.setUsername(REGULAR_USERNAME);
        user.setPassword(passwordEncoder.encode(PASSWORD));
        userRepository.save(user);

        adminUser = new MyUser();
        adminUser.setUsername(ADMIN_USERNAME);
        adminUser.setRole(Role.ADMIN);
        adminUser.setPassword(passwordEncoder.encode(PASSWORD));
        userRepository.save(adminUser);

    }

    private String obtainToken(String username) throws Exception {
        TokenRequest loginRequest = new TokenRequest(username, PASSWORD);

        String responseJson = mockMvc.perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(responseJson).get("accessToken").asText();
    }

    @Nested 
    class TestMethodSecurityWiring {
        
        @Test 
        void getAllUsers_AsRegularUser_returns403() throws Exception {
            // arrange
            String token = obtainToken(REGULAR_USERNAME); 

            // act & assert
            mockMvc.perform(get("/users")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isForbidden());
        }

        @Test 
        void getAllUsers_AsAdmin_returns200() throws Exception {
            // arrange
            String token = obtainToken(ADMIN_USERNAME); 

            // act & assert
            mockMvc.perform(get("/users")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk());
        }

        @Test 
        void getSelf_AsRegularUser_returns200() throws Exception {
            // arrange
            String token = obtainToken(REGULAR_USERNAME); 

            // act & assert
            mockMvc.perform(get("/users/{id}", user.getId())
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk());

        }

        @Test 
        void getOther_AsRegularUser_returns403() throws Exception {
            // arrange
            String token = obtainToken(REGULAR_USERNAME); 

            // act & assert
            mockMvc.perform(get("/users/{id}", adminUser.getId())
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isForbidden());
        }

        @Test
        void updateSelf_AsRegularUser_returns200() throws Exception {
            // arrange
            String token = obtainToken(REGULAR_USERNAME);
            UserUpdateRequest request = new UserUpdateRequest("renamed-user", "new-password");

            // act & assert
            mockMvc.perform(put("/users/{id}", user.getId())
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isOk());
        }

        @Test
        void updateOther_AsRegularUser_returns403AndLeavesUserUnchanged() throws Exception {
            // arrange
            String token = obtainToken(REGULAR_USERNAME);
            UserUpdateRequest request = new UserUpdateRequest("hijacked-admin", "new-password");

            // act & assert
            mockMvc.perform(put("/users/{id}", adminUser.getId())
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isForbidden());

            // the status code alone doesn't prove nothing was written: check the DB too
            MyUser unchanged = userRepository.findById(adminUser.getId()).orElseThrow();
            assertEquals(ADMIN_USERNAME, unchanged.getUsername());
        }

        @Test
        void deleteSelf_AsRegularUser_returns403() throws Exception {
            // arrange
            String token = obtainToken(REGULAR_USERNAME);

            // act & assert
            mockMvc.perform(delete("/users/{id}", user.getId())
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isForbidden());

            assertTrue(userRepository.existsById(user.getId()));
        }

        @Test
        void deleteOther_AsAdmin_returns200() throws Exception {
            // arrange
            String token = obtainToken(ADMIN_USERNAME);

            // act & assert
            mockMvc.perform(delete("/users/{id}", user.getId())
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk());

            assertFalse(userRepository.existsById(user.getId()));
        }
    }

    
}
