package Scheduler.Repository;

import Scheduler.Entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findAllByMembers_User_Id(Long userId);
}
