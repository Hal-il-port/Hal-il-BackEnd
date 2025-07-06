package Scheduler.Dto.Todo;

import Scheduler.Entity.Todo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Builder
public class TodoResponseDto {
    private Long id;
    private String content;
    private LocalDate dueDate;
    private Todo.Status status;
    private Todo.TodoType type;
    private String author; // 작성자명 추가 (팀 일정일 경우)
}
