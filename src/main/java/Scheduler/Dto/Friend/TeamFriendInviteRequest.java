package Scheduler.Dto.Friend;

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
    private Long teamId;
    private List<Long> friendIds;
}
