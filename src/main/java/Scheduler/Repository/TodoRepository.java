package Scheduler.Repository;

import Scheduler.Entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findAllByUserId(Long userId);
    List<Todo> findAllByTeamId(Long teamId);
}
