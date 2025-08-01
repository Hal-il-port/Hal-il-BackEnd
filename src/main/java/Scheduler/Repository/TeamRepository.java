package Scheduler.Repository;

import Scheduler.Entity.Team;
import Scheduler.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findAllByMembers_User_Id(Long userId);
    @Query("SELECT tm.user FROM TeamMember tm WHERE tm.team.id = :teamId")
    List<User> findMembersByTeamId(@Param("teamId") Long teamId);
}
