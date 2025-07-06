package Scheduler.Repository;

import Scheduler.Entity.Team;
import Scheduler.Entity.TeamInvitation;
import Scheduler.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamInvitationRepository extends JpaRepository<TeamInvitation, Long> {
    boolean existsByInviteToken(String inviteToken); //초대유효 검증
    boolean existsByTeamAndToUser(Team team, User toUser);
}
