package Scheduler.Service;

import Scheduler.Entity.TeamInvitation;
import Scheduler.Entity.TeamMember;
import Scheduler.Entity.User;
import Scheduler.Repository.TeamInvitationRepository;
import Scheduler.Repository.TeamMemberRepository;
import Scheduler.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InviteSignupService {

    private final UserRepository userRepository;
    private final TeamInvitationRepository teamInvitationRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final PasswordEncoder passwordEncoder;

    public void signup(String name, String email, String password, String inviteToken) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        User user = User.builder()
                .email(email)
                .name(name)
                .password(passwordEncoder.encode(password))
                .build();
        userRepository.save(user);

        if (inviteToken != null && !inviteToken.isEmpty()) {
            TeamInvitation invitation = teamInvitationRepository.findAll()
                    .stream()
                    .filter(inv -> inviteToken.equals(inv.getInviteToken()) && inv.getExpiresAt().isAfter(LocalDateTime.now()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 초대 토큰입니다."));

            TeamMember member = TeamMember.builder()
                    .team(invitation.getTeam())
                    .user(user)
                    .role("MEMBER")
                    .build();
            teamMemberRepository.save(member);
        }
    }
}
