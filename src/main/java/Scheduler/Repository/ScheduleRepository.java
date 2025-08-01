package Scheduler.Repository;

import Scheduler.Entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findAllByUserId(Long userId);
    List<Schedule> findAllByTeamId(Long teamId);
    List<Schedule> findByDueDate(LocalDate dueDate);
}
