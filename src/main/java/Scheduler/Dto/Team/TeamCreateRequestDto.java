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
public class TeamCreateRequestDto {

    @Schema(description = "팀 이름", example = "개발팀")
    private String name;

    @Schema(description = "팀에 초대할 유저 ID 목록", example = "[1, 2, 3]")
    private List<Long> userIds;
}
