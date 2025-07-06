package Scheduler.Dto.Todo;

import Scheduler.Entity.Todo;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TodoRequestDto {
    private String content;
    private LocalDate dueDate;
    private Todo.Status status;
    private Todo.TodoType type;
    private Long teamId; // 팀 일정일 경우 필요
}
