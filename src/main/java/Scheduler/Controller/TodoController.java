package Scheduler.Controller;

import Scheduler.Dto.Todo.TodoRequestDto;
import Scheduler.Dto.Todo.TodoResponseDto;
import Scheduler.Service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;

    @PostMapping
    public ResponseEntity<TodoResponseDto> create(@AuthenticationPrincipal UserDetails userDetails,
                                                  @RequestBody TodoRequestDto dto) {
        return ResponseEntity.ok(todoService.create(userDetails.getUsername(), dto));
    }

    @GetMapping("/my")
    public ResponseEntity<List<TodoResponseDto>> getMy(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(todoService.getMyTodos(userDetails.getUsername()));
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<TodoResponseDto>> getTeam(@PathVariable Long teamId) {
        return ResponseEntity.ok(todoService.getTeamTodos(teamId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoResponseDto> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(todoService.getOne(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoResponseDto> update(@PathVariable Long id, @RequestBody TodoRequestDto dto) {
        return ResponseEntity.ok(todoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        todoService.delete(id);
        return ResponseEntity.ok().build();
    }
}
