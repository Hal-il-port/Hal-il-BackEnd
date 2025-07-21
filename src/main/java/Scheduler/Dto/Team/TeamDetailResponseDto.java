package Scheduler.Dto.Team;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamDetailResponseDto {

    @Schema(description = "팀 ID", example = "1")
    private Long id;

    @Schema(description = "팀 이름", example = "개발팀")
    private String name;

    @Schema(description = "팀 생성 일자", example = "2025-07-20T14:22:00")
    private String createdAt;

    @Schema(description = "팀 멤버 목록")
    private List<MemberDto> members;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberDto {

        @Schema(description = "멤버 유저 ID", example = "2")
        private Long userId;

        @Schema(description = "멤버 이름", example = "김원종")
        private String name;

        @Schema(description = "멤버 역할", example = "팀장")
        private String role;
    }
}
