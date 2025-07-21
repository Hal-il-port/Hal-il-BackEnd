package Scheduler.Controller;

import Scheduler.Dto.Team.*;
import Scheduler.Dto.UserSearchResponseDto;
import Scheduler.Service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
@Tag(name = "팀", description = "팀 생성, 조회, 초대, 삭제, 초대 수락/거절 API")
public class TeamController {

    private final TeamService teamService;

    @Operation(summary = "팀 초대용 유저 검색", description = "팀에 초대 가능한 유저를 키워드로 검색합니다.")
    @GetMapping("/search-users")
    public ResponseEntity<List<UserSearchResponseDto>> searchUsersForTeamInvite(
            @Parameter(description = "검색 키워드", example = "김원종") @RequestParam String keyword,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        List<UserSearchResponseDto> users = teamService.searchUsersForTeamInvite(keyword, userDetails.getUsername());
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "팀 생성", description = "새로운 팀을 생성합니다.")
    @PostMapping("/create")
    public ResponseEntity<TeamResponseDto> createTeam(
            @RequestBody TeamCreateRequestDto request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        TeamResponseDto response = teamService.createTeam(userDetails.getUsername(), request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내 팀 목록 조회", description = "내가 속한 팀 목록을 조회합니다.")
    @GetMapping("/my")
    public ResponseEntity<List<TeamResponseDto>> getMyTeams(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(teamService.getMyTeams(userDetails.getUsername()));
    }

    @Operation(summary = "팀 상세 조회", description = "팀 ID로 팀 상세 정보를 조회합니다.")
    @GetMapping("/{teamId}")
    public ResponseEntity<TeamDetailResponseDto> getTeamDetail(
            @Parameter(description = "팀 ID", example = "1") @PathVariable Long teamId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(teamService.getTeamDetail(teamId, userDetails.getUsername()));
    }

    @Operation(summary = "팀 멤버 초대", description = "팀에 멤버를 초대합니다.")
    @PostMapping("/invite")
    public ResponseEntity<Void> inviteMembers(
            @RequestBody TeamInviteRequestDto request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        teamService.inviteUsersToTeam(userDetails.getUsername(), request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "팀 삭제", description = "팀을 삭제합니다.")
    @DeleteMapping("/{teamId}/delete")
    public ResponseEntity<Void> deleteTeam(
            @Parameter(description = "팀 ID", example = "1") @PathVariable Long teamId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        teamService.deleteTeam(teamId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "팀 탈퇴", description = "팀에서 본인이 탈퇴합니다.")
    @DeleteMapping("/{teamId}/leave")
    public ResponseEntity<Void> leaveTeam(
            @Parameter(description = "팀 ID", example = "1") @PathVariable Long teamId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        teamService.leaveTeam(teamId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "팀 초대 수락", description = "팀 초대 요청을 수락합니다.")
    @PostMapping("/invitations/{invitationId}/accept")
    public ResponseEntity<Void> acceptInvitation(
            @Parameter(description = "초대 ID", example = "10") @PathVariable Long invitationId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        teamService.acceptInvitation(invitationId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "내 팀 초대 목록 조회", description = "내가 받은 팀 초대 목록을 조회합니다.")
    @GetMapping("/invitations")
    public ResponseEntity<List<TeamInviteResponseDto>> getMyInvitations(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(teamService.getMyInvitations(userDetails.getUsername()));
    }

    @Operation(summary = "팀 초대 거절", description = "받은 팀 초대를 거절합니다.")
    @DeleteMapping("/invitations/{invitationId}/reject")
    public ResponseEntity<Void> rejectInvitation(
            @Parameter(description = "초대 ID", example = "10") @PathVariable Long invitationId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        teamService.rejectInvitation(invitationId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
