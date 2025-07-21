package Scheduler.Dto.Friend;

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
public class TeamFriendInviteRequest {

    @Schema(description = "팀 ID", example = "10")
    private Long teamId;

    @Schema(description = "초대할 친구 ID 목록", example = "[1, 2, 3]")
    private List<Long> friendIds;
}
