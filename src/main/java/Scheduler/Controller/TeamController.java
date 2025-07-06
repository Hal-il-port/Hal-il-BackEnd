package Scheduler.Controller;

import Scheduler.Dto.Team.*;
import Scheduler.Service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @PostMapping("/create")
    public ResponseEntity<TeamResponseDto> createTeam(@RequestBody TeamCreateRequestDto request,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        TeamResponseDto response = teamService.createTeam(userDetails.getUsername(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<TeamResponseDto>> getMyTeams(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(teamService.getMyTeams(userDetails.getUsername()));
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<TeamDetailResponseDto> getTeamDetail(@PathVariable Long teamId,
                                                               @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(teamService.getTeamDetail(teamId, userDetails.getUsername()));
    }

    @PostMapping("/{teamId}/invite")
    public ResponseEntity<List<TeamInviteResponseDto>> inviteMembers(@PathVariable Long teamId,
                                                                     @RequestBody TeamInviteRequestDto request,
                                                                     @AuthenticationPrincipal UserDetails userDetails) {
        List<TeamInviteResponseDto> result = teamService.inviteMultipleMembers(teamId, userDetails.getUsername(), request);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{teamId}/delete")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long teamId,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        teamService.deleteTeam(teamId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{teamId}/leave")
    public ResponseEntity<Void> leaveTeam(@PathVariable Long teamId,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        teamService.leaveTeam(teamId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}
