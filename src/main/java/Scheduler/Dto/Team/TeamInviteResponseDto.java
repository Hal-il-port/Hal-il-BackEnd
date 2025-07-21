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
public class TeamInviteResponseDto {

    @Schema(description = "초대 ID", example = "10")
    private Long invitationId;

    @Schema(description = "팀 ID", example = "1")
    private Long teamId;

    @Schema(description = "팀 이름", example = "개발팀")
    private String teamName;

    @Schema(description = "초대 보낸 사람 이름", example = "김원종")
    private String fromUserName;

    @Schema(description = "초대 토큰 (비회원용)", example = "TOKEN1234")
    private String inviteToken;

    @Schema(description = "초대 URL (비회원용)", example = "https://example.com/invite/abc123")
    private String inviteUrl;
}
