package org.example.todoapp.service;

import org.example.todoapp.dto.TodoCreateRequest;
import org.example.todoapp.dto.TodoResponse;
import org.example.todoapp.dto.TodoUpdateRequest;
import org.example.todoapp.entity.MyUser;
import org.example.todoapp.entity.Todo;
import org.example.todoapp.exception.EntityNotFoundException;
import org.example.todoapp.exception.PermissionDeniedException;
import org.example.todoapp.exception.TodoIdNotFoundException;
import org.example.todoapp.repository.TodoRepository;
import org.example.todoapp.exception.TodoAlreadyExcistsException;
import org.example.todoapp.repository.UserRepository;
import org.example.todoapp.security.UserPrincipal;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class TodoService {
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    public TodoService(TodoRepository todoRepository, UserRepository userRepository) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
    }

    private static TodoResponse toResponse(Todo todo){
        MyUser owner = todo.getOwner();
        String ownerId = owner != null ? owner.getId() : null; // prevent NullPointerException
        return new TodoResponse(todo.getId(), todo.getTask(), todo.getDue(), todo.isDone(), ownerId);
    }

    public TodoResponse update(String id, TodoUpdateRequest request){
        Todo todo = todoRepository.findById(id).orElseThrow(TodoIdNotFoundException::new);
        todo.setDone(request.done());
        todo.setDue(request.due());
        todo.setTask(request.task());
        Todo updated = todoRepository.save(todo);
        return toResponse(updated);
    }


    public TodoResponse create (TodoCreateRequest todo, UserPrincipal principal){
        MyUser creator = userRepository.findById(principal.getUserId()).orElseThrow(EntityNotFoundException::new);

        Optional<Todo> todoCheck = todoRepository.findByTask(todo.task());
        if(todoCheck.isPresent()){
            throw new TodoAlreadyExcistsException();
        }
        Todo toSave = new Todo(todo.task(), todo.due(), creator);
        Todo saved = todoRepository.save(toSave);
        return toResponse(saved);
    }

    public TodoResponse read(String id){
        Todo todo = todoRepository.findById(id).orElseThrow(TodoIdNotFoundException::new);
        return toResponse(todo);
    }

    public List<TodoResponse> readAll(UserPrincipal principal){
        if(principal.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))){
            return todoRepository.findAll().stream().map(TodoService::toResponse).toList();
        }

        MyUser owner = userRepository.findById(principal.getUserId()).orElseThrow(EntityNotFoundException::new);
        return todoRepository.findByOwner(owner).stream().map(TodoService::toResponse).toList();
    }

    public TodoResponse deleteByID(String id){
        Todo todo = todoRepository.findById(id).orElseThrow(TodoIdNotFoundException::new);
        todoRepository.deleteById(id);
        return toResponse(todo);

    }
}
