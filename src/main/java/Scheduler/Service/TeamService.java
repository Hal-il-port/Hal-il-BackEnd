package Scheduler.Service;

import Scheduler.Dto.Team.*;
import Scheduler.Entity.Team;
import Scheduler.Entity.TeamInvitation;
import Scheduler.Entity.TeamMember;
import Scheduler.Entity.User;
import Scheduler.Repository.TeamInvitationRepository;
import Scheduler.Repository.TeamMemberRepository;
import Scheduler.Repository.TeamRepository;
import Scheduler.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamInvitationRepository teamInvitationRepository;
    private final UserRepository userRepository;

    public TeamResponseDto createTeam(String email, TeamCreateRequestDto request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        Team team = Team.builder()
                .name(request.getName())
                .build();
        teamRepository.save(team);

        TeamMember member = TeamMember.builder()
                .team(team)
                .user(user)
                .role("LEADER")
                .build();
        teamMemberRepository.save(member);

        return new TeamResponseDto(team.getId(), team.getName(), team.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }

    public List<TeamResponseDto> getMyTeams(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        return teamRepository.findAllByMembers_User_Id(user.getId()).stream()
                .map(team -> new TeamResponseDto(team.getId(), team.getName(), team.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))))
                .collect(Collectors.toList());
    }

    public TeamDetailResponseDto getTeamDetail(Long teamId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        if (!teamMemberRepository.existsByTeamIdAndUserId(teamId, user.getId())) {
            throw new IllegalArgumentException("팀에 속해있지 않습니다");
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다"));

        List<TeamDetailResponseDto.MemberDto> members = teamMemberRepository.findAllByTeamIdWithUser(teamId)
                .stream().map(tm -> new TeamDetailResponseDto.MemberDto(
                        tm.getUser().getId(),
                        tm.getUser().getName(),
                        tm.getRole()
                )).collect(Collectors.toList());

        return new TeamDetailResponseDto(team.getId(), team.getName(),
                team.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), members);
    }

    public List<TeamInviteResponseDto> inviteMultipleMembers(Long teamId, String requesterEmail, TeamInviteRequestDto request) {
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new IllegalArgumentException("요청자 없음"));

        if (!teamMemberRepository.existsByTeamIdAndUserId(teamId, requester.getId())) {
            throw new IllegalArgumentException("해당 팀의 멤버가 아닙니다");
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀 없음"));

        List<TeamInviteResponseDto> result = new ArrayList<>();

        for (String email : request.getEmails()) {
            String token = UUID.randomUUID().toString();

            TeamInvitation invitation = TeamInvitation.builder()
                    .email(email)
                    .inviteToken(token)
                    .expiresAt(LocalDateTime.now().plusDays(3))
                    .team(team)
                    .build();
            teamInvitationRepository.save(invitation);

            String inviteUrl = "http://localhost:8080/api/signup?token=" + token;
            result.add(new TeamInviteResponseDto(email, inviteUrl));

            System.out.println("[초대 로그] " + email + " → " + inviteUrl);
        }

        return result;
    }

    public void deleteTeam(Long teamId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀 없음"));

        TeamMember member = teamMemberRepository.findAllByTeamIdWithUser(teamId).stream()
                .filter(m -> m.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("팀 멤버가 아님"));

        if (!"LEADER".equals(member.getRole())) {
            throw new IllegalArgumentException("팀 삭제 권한 없음 (리더만 가능)");
        }

        teamRepository.delete(team);
    }

    public void leaveTeam(Long teamId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        if (!teamMemberRepository.existsByTeamIdAndUserId(teamId, user.getId())) {
            throw new IllegalArgumentException("팀에 속해있지 않음");
        }

        teamMemberRepository.deleteByTeamIdAndUserId(teamId, user.getId());
    }
}
