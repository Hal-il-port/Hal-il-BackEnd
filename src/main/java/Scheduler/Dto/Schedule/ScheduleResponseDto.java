package Scheduler.Dto.Schedule;

import Scheduler.Entity.Schedule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Builder
public class ScheduleResponseDto {
    @Schema(description = "일정 ID", example = "10")
    private Long id;

    @Schema(description = "일정 내용", example = "프론트엔드 구현 마감")
    private String content;

    @Schema(description = "일정 마감일", example = "2025-07-31")
    private LocalDate dueDate;

    @Schema(description = "일정 상태 (IN_PROGRESS 또는 DONE)", example = "DONE")
    private Schedule.Status status;

    @Schema(description = "일정 유형 (PERSONAL 또는 TEAM)", example = "TEAM")
    private Schedule.ScheduleType type;

    @Schema(description = "작성자 이름 (팀 일정일 경우 노출)", example = "김원종")
    private String author;
}
