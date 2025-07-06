package Scheduler.Dto.Team;

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
    private Long id;
    private String name;
    private String createdAt;
    private List<MemberDto> members;

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberDto {
        private Long userId;
        private String name;
        private String role;
    }
}
