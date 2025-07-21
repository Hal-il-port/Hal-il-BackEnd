package Scheduler.Dto.Schedule;

import Scheduler.Entity.Schedule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ScheduleRequestDto {

    @Schema(description = "일정 내용", example = "프론트엔드 구현 마감")
    private String content;

    @Schema(description = "일정 마감일", example = "2025-07-31")
    private LocalDate dueDate;

    @Schema(description = "일정 상태 (IN_PROGRESS 또는 DONE)", example = "IN_PROGRESS")
    private Schedule.Status status;

    @Schema(description = "일정 유형 (PERSONAL 또는 TEAM)", example = "TEAM")
    private Schedule.ScheduleType type;

    @Schema(description = "팀 ID (팀 일정일 경우 필수)", example = "1")
    private Long teamId;
}
