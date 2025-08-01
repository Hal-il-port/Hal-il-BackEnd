package Scheduler.Service;

import Scheduler.Dto.Team.*;
import Scheduler.Dto.User.UserSearchResponseDto;
import Scheduler.Entity.*;
import Scheduler.Repository.TeamInvitationRepository;
import Scheduler.Repository.TeamMemberRepository;
import Scheduler.Repository.TeamRepository;
import Scheduler.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamInvitationRepository teamInvitationRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public List<UserSearchResponseDto> searchUsersForTeamInvite(String keyword, String email) {
        User requester = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        return userRepository.findByNameContainingOrEmailContaining(keyword, keyword).stream()
                .filter(user -> !user.getId().equals(requester.getId()))
                .map(user -> new UserSearchResponseDto(user.getId(), user.getName(), user.getEmail()))
                .collect(Collectors.toList());
    }

    public TeamResponseDto createTeam(String inviterEmail, TeamCreateRequestDto request) {
        User inviter = userRepository.findByEmail(inviterEmail)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        Team team = Team.builder().name(request.getName()).build();
        teamRepository.save(team);

        // 리더는 즉시 팀 멤버로 등록
        teamMemberRepository.save(TeamMember.builder()
                .team(team).user(inviter).role("LEADER").build());

        // 초대는 초대 테이블에 저장 (수락 여부는 이후 처리)
        inviteUsersToTeam(inviterEmail, new TeamInviteRequestDto(null,team.getId(), request.getUserIds()));

        return new TeamResponseDto(
                team.getId(),
                team.getName(),
                team.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        );
    }

    public void inviteUsersToTeam(String inviterEmail, TeamInviteRequestDto request) {
        User inviter = userRepository.findByEmail(inviterEmail)
                .orElseThrow(() -> new IllegalArgumentException("초대자 정보 없음"));
        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new IllegalArgumentException("팀 없음"));

        for (Long userId : request.getUserIds()) {
            User invitee = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 유저 없음"));

            boolean alreadyInvited = teamInvitationRepository.existsByTeamAndToUser(team, invitee);
            if (alreadyInvited) continue;

            String token = UUID.randomUUID().toString();

            TeamInvitation invitation = TeamInvitation.builder()
                    .team(team)
                    .fromUser(inviter)
                    .toUser(invitee)
                    .inviteToken(token)
                    .accepted(false)
                    .build();
            teamInvitationRepository.save(invitation);

            notificationService.sendNotification(
                    invitee.getId(),
                    NotificationType.TEAM_INVITE,
                    inviter.getName() + "님이 '" + team.getName() + "'에 초대했습니다.",
                    team.getId()
            );
        }
    }

    public List<TeamInviteResponseDto> getMyInvitations(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보 없음"));

        List<TeamInvitation> invitations = teamInvitationRepository.findByToUserAndAcceptedFalse(user);

        return invitations.stream()
                .map(invite -> {
                    TeamInviteResponseDto dto = new TeamInviteResponseDto();
                    dto.setInvitationId(invite.getId());
                    dto.setTeamId(invite.getTeam().getId());
                    dto.setTeamName(invite.getTeam().getName());
                    dto.setFromUserName(invite.getFromUser().getName());
                    dto.setInviteToken(invite.getInviteToken());
                    // inviteUrl은 필요하면 여기서 set 해주세요
                    return dto;
                })
                .collect(Collectors.toList());
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

//    public List<TeamInviteResponseDto> inviteMultipleMembers(Long teamId, String requesterEmail, TeamInviteRequestDto request) {
//        User requester = userRepository.findByEmail(requesterEmail)
//                .orElseThrow(() -> new IllegalArgumentException("요청자 없음"));
//
//        if (!teamMemberRepository.existsByTeamIdAndUserId(teamId, requester.getId())) {
//            throw new IllegalArgumentException("해당 팀의 멤버가 아닙니다");
//        }
//
//        Team team = teamRepository.findById(teamId)
//                .orElseThrow(() -> new IllegalArgumentException("팀 없음"));
//
//        List<TeamInviteResponseDto> result = new ArrayList<>();
//
//        for (String email : request.getEmails()) {
//            String token = UUID.randomUUID().toString();
//
//            TeamInvitation invitation = TeamInvitation.builder()
//                    .email(email)
//                    .inviteToken(token)
//                    .expiresAt(LocalDateTime.now().plusDays(3))
//                    .team(team)
//                    .build();
//            teamInvitationRepository.save(invitation);
//
//            String inviteUrl = "http://localhost:8080/api/signup?token=" + token;
//            result.add(new TeamInviteResponseDto(email, inviteUrl));
//
//            System.out.println("[초대 로그] " + email + " → " + inviteUrl);
//        }
//
//        return result;
//    }

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

    public void acceptInvitation(Long invitationId, String userEmail) {
        TeamInvitation invitation = teamInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new IllegalArgumentException("초대가 존재하지 않습니다."));

        if (!invitation.getToUser().getEmail().equals(userEmail)) {
            throw new IllegalStateException("초대받은 사용자만 수락할 수 있습니다.");
        }

        if (invitation.isAccepted()) return;

        invitation.setAccepted(true);
        teamInvitationRepository.save(invitation);

        teamMemberRepository.save(
                TeamMember.builder()
                        .team(invitation.getTeam())
                        .user(invitation.getToUser())
                        .role("MEMBER")
                        .build()
        );

        notificationService.sendNotification(
                invitation.getFromUser().getId(),
                NotificationType.TEAM_INVITE_ACCEPT,
                invitation.getToUser().getName() + "님이 초대를 수락했습니다.",
                invitation.getTeam().getId()
        );
    }

    public void rejectInvitation(Long invitationId, String userEmail) {
        TeamInvitation invitation = teamInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new IllegalArgumentException("초대가 존재하지 않습니다."));

        if (!invitation.getToUser().getEmail().equals(userEmail)) {
            throw new IllegalStateException("초대받은 사용자만 거절할 수 있습니다.");
        }

        teamInvitationRepository.delete(invitation);
    }
}
