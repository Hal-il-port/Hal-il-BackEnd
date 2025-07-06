package Scheduler.Service;

import Scheduler.Dto.Todo.TodoRequestDto;
import Scheduler.Dto.Todo.TodoResponseDto;
import Scheduler.Entity.Todo;
import Scheduler.Entity.User;
import Scheduler.Repository.TeamRepository;
import Scheduler.Repository.TodoRepository;
import Scheduler.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    @Transactional
    public TodoResponseDto create(String email, TodoRequestDto dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        Todo.TodoType type = dto.getType();

        Todo todo = Todo.builder()
                .content(dto.getContent())
                .dueDate(dto.getDueDate())
                .status(dto.getStatus())
                .type(type)
                .user(type == Todo.TodoType.PERSONAL ? user : null)
                .team(type == Todo.TodoType.TEAM ? teamRepository.findById(dto.getTeamId())
                        .orElseThrow(() -> new IllegalArgumentException("팀 없음")) : null)
                .build();

        return toDto(todoRepository.save(todo));
    }

    public List<TodoResponseDto> getMyTodos(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));
        return todoRepository.findAllByUserId(user.getId()).stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<TodoResponseDto> getTeamTodos(Long teamId) {
        return todoRepository.findAllByTeamId(teamId).stream().map(this::toDto).collect(Collectors.toList());
    }

    public TodoResponseDto getOne(Long id) {
        return toDto(todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("일정 없음")));
    }

    @Transactional
    public TodoResponseDto update(Long id, TodoRequestDto dto) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("일정 없음"));
        todo.setContent(dto.getContent());
        todo.setDueDate(dto.getDueDate());
        todo.setStatus(dto.getStatus());
        return toDto(todo);
    }

    @Transactional
    public void delete(Long id) {
        todoRepository.deleteById(id);
    }

    private TodoResponseDto toDto(Todo todo) {
        return TodoResponseDto.builder()
                .id(todo.getId())
                .content(todo.getContent())
                .dueDate(todo.getDueDate())
                .status(todo.getStatus())
                .type(todo.getType())
                .author(
                        todo.getType() == Todo.TodoType.TEAM && todo.getUser() != null
                                ? todo.getUser().getName() // 또는 .getEmail()
                                : null
                )
                .build();
    }
}
