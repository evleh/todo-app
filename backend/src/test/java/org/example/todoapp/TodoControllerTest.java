package org.example.todoapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.todoapp.dto.TodoCreateRequest;
import org.example.todoapp.dto.TokenRequest;
import org.example.todoapp.entity.MyUser;
import org.example.todoapp.entity.Role;
import org.example.todoapp.entity.Todo;
import org.example.todoapp.repository.TodoRepository;
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

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String PASSWORD = "test-password";
    private static final String OWNER_USERNAME = "subtask-owner";
    private static final String OTHER_USERNAME = "subtask-other";

    private MyUser owner;
    private Todo parentTodo;

    @BeforeEach
    void setUp() {
        // delete todos before users: Todo.owner is a FK to MyUser
        todoRepository.deleteAll();
        userRepository.deleteAll();

        owner = new MyUser();
        owner.setUsername(OWNER_USERNAME);
        owner.setPassword(passwordEncoder.encode(PASSWORD));
        userRepository.save(owner);

        MyUser other = new MyUser();
        other.setUsername(OTHER_USERNAME);
        other.setPassword(passwordEncoder.encode(PASSWORD));
        userRepository.save(other);

        parentTodo = todoRepository.save(new Todo("parent task", LocalDate.now(), owner));
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
    class CreateSubtaskTests {

        @Test
        void ownerCreatesSubtask_returns201WithSubtask() throws Exception {
            // arrange
            String token = obtainToken(OWNER_USERNAME);
            TodoCreateRequest request = new TodoCreateRequest("buy milk", LocalDate.now());

            // act & assert
            mockMvc.perform(post("/todos/{parentId}/subtasks", parentTodo.getId())
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.task").value("buy milk"))
                    .andExpect(jsonPath("$.ownerId").value(owner.getId()));
        }

        @Test
        void otherUserCreatesSubtaskUnderSomeoneElsesTodo_returns403() throws Exception {
            // arrange
            String token = obtainToken(OTHER_USERNAME);
            TodoCreateRequest request = new TodoCreateRequest("buy milk", LocalDate.now());

            // act & assert
            mockMvc.perform(post("/todos/{parentId}/subtasks", parentTodo.getId())
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }

        @Test
        void nonexistentParent_returns404() throws Exception {
            // arrange
            String token = obtainToken(OWNER_USERNAME);
            TodoCreateRequest request = new TodoCreateRequest("buy milk", LocalDate.now());

            // act & assert
            mockMvc.perform(post("/todos/{parentId}/subtasks", "nonexistent-todo-id")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void parentIsAlreadyASubtask_returns400() throws Exception {
            // arrange
            String token = obtainToken(OWNER_USERNAME);
            Todo alreadySubtask = todoRepository.save(new Todo("nested task", LocalDate.now(), owner, parentTodo));
            TodoCreateRequest request = new TodoCreateRequest("buy milk", LocalDate.now());

            // act & assert
            mockMvc.perform(post("/todos/{parentId}/subtasks", alreadySubtask.getId())
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class ViewEmbeddedSubtasksTests {

        @Test
        void getParentById_asOwner_returnsSubtaskEmbedded() throws Exception {
            // arrange
            String token = obtainToken(OWNER_USERNAME);
            Todo subtask = todoRepository.save(new Todo("buy milk", LocalDate.now(), owner, parentTodo));

            // act & assert
            mockMvc.perform(get("/todos/{id}", parentTodo.getId())
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.subtasks", hasSize(1)))
                    .andExpect(jsonPath("$.subtasks[0].id").value(subtask.getId()))
                    .andExpect(jsonPath("$.subtasks[0].task").value("buy milk"))
                    .andExpect(jsonPath("$.subtasks[0].parentId").value(parentTodo.getId()))
                    .andExpect(jsonPath("$.subtasks[0].subtasks", hasSize(0)));
        }

        @Test
        void getParentById_withNoSubtasks_returnsEmptySubtasksList() throws Exception {
            // arrange
            String token = obtainToken(OWNER_USERNAME);

            // act & assert
            mockMvc.perform(get("/todos/{id}", parentTodo.getId())
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.subtasks").exists())
                    .andExpect(jsonPath("$.subtasks", hasSize(0)));
        }

        @Test
        void getSubtaskById_asOwner_returnsEmptySubtasksList() throws Exception {
            // arrange
            String token = obtainToken(OWNER_USERNAME);
            Todo subtask = todoRepository.save(new Todo("buy milk", LocalDate.now(), owner, parentTodo));

            // act & assert
            mockMvc.perform(get("/todos/{id}", subtask.getId())
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.parentId").value(parentTodo.getId()))
                    .andExpect(jsonPath("$.subtasks").exists())
                    .andExpect(jsonPath("$.subtasks", hasSize(0)));
        }

        @Test
        void getAllTodos_asOwner_excludesSubtaskAsStandaloneEntry() throws Exception {
            // arrange
            String token = obtainToken(OWNER_USERNAME);
            todoRepository.save(new Todo("buy milk", LocalDate.now(), owner, parentTodo));

            // act & assert — only the top-level parentTodo shows up, the subtask does not appear as a separate entry
            mockMvc.perform(get("/todos")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id").value(parentTodo.getId()));
        }

        @Test
        void getAllTodos_asAdmin_excludesSubtaskAsStandaloneEntryAcrossAllUsers() throws Exception {
            // arrange
            MyUser admin = new MyUser();
            admin.setUsername("subtask-admin");
            admin.setPassword(passwordEncoder.encode(PASSWORD));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            String token = obtainToken("subtask-admin");

            todoRepository.save(new Todo("buy milk", LocalDate.now(), owner, parentTodo));

            // act & assert — admin sees every top-level todo, but never a subtask as a standalone entry
            mockMvc.perform(get("/todos")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id").value(parentTodo.getId()));
        }
    }
}
