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
public class TeamInviteRequestDto {

    @Schema(description = "초대할 이메일 목록", example = "[\"friend1@example.com\", \"friend2@example.com\"]")
    private List<String> emails;

    @Schema(description = "초대할 팀 ID", example = "1")
    private Long teamId;

    @Schema(description = "초대할 유저 ID 목록 (친구 또는 검색 유저)", example = "[4, 5, 6]")
    private List<Long> userIds;
}
