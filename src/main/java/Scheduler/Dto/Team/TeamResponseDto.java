package Scheduler.Dto.Team;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamResponseDto {

    @Schema(description = "팀 ID", example = "1")
    private Long id;

    @Schema(description = "팀 이름", example = "개발팀")
    private String name;

    @Schema(description = "팀 생성 일자", example = "2025-07-20T14:22:00")
    private String createdAt;
}
