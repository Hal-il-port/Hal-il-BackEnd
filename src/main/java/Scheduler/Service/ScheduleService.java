package Scheduler.Service;

import Scheduler.Dto.Schedule.ScheduleRequestDto;
import Scheduler.Dto.Schedule.ScheduleResponseDto;
import Scheduler.Entity.NotificationType;
import Scheduler.Entity.Schedule;
import Scheduler.Entity.Team;
import Scheduler.Entity.User;
import Scheduler.Repository.TeamRepository;
import Scheduler.Repository.ScheduleRepository;
import Scheduler.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 0 * * *")
    public void notifyDeadlines() {
        LocalDate targetDate = LocalDate.now().plusDays(3);

        // 마감일이 정확히 3일 남은 일정 조회
        List<Schedule> schedules = scheduleRepository.findByDueDate(targetDate);

        for (Schedule schedule : schedules) {
            if (schedule.getType() == Schedule.ScheduleType.PERSONAL
                    || (schedule.getType() == Schedule.ScheduleType.TEAM && schedule.getUser() != null)) {
                // 개인 일정이거나, 팀 일정이면서 일정 작성자가 있을 때는 그 사람에게만 알림
                NotificationType type = schedule.getType() == Schedule.ScheduleType.PERSONAL
                        ? NotificationType.PERSONAL_DEADLINE
                        : NotificationType.TEAM_DEADLINE;

                String prefix = schedule.getType() == Schedule.ScheduleType.PERSONAL ? "[개인 일정]" : "[팀 일정]";

                notificationService.sendNotification(
                        schedule.getUser().getId(),
                        type,
                        prefix + " '" + schedule.getContent() + "' 마감 3일 전입니다.",
                        schedule.getId()
                );
            }
            // 만약 팀 전체 알림이 필요하면 별도 조건으로 처리 가능
        }
    }

    public ScheduleResponseDto create(String email, ScheduleRequestDto dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        Schedule.ScheduleType type = dto.getType();

        Schedule schedule = Schedule.builder()
                .content(dto.getContent())
                .dueDate(dto.getDueDate())
                .status(dto.getStatus())
                .type(type)
                .user(user)
                .team(type == Schedule.ScheduleType.TEAM
                        ? teamRepository.findById(dto.getTeamId())
                        .orElseThrow(() -> new IllegalArgumentException("팀 없음"))
                        : null)
                .build();

        Schedule saved = scheduleRepository.save(schedule);

        // 팀 일정 생성 알림
        if (type == Schedule.ScheduleType.TEAM) {
            Team team = saved.getTeam();
            List<User> members = teamRepository.findMembersByTeamId(team.getId());
            for (User member : members) {
                if (!member.getId().equals(user.getId())) { // 작성자 제외
                    notificationService.sendNotification(
                            member.getId(),
                            NotificationType.TEAM_SCHEDULE_CREATE,
                            "'" + team.getName() + "' " + user.getName() + "님이 일정을 생성하였습니다.",
                            null
                    );
                }
            }
        }

        return toDto(saved);
    }

    public List<ScheduleResponseDto> getMySchedule(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));
        return scheduleRepository.findAllByUserId(user.getId()).stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<ScheduleResponseDto> getTeamSchedule(Long teamId) {
        return scheduleRepository.findAllByTeamId(teamId).stream().map(this::toDto).collect(Collectors.toList());
    }

    public ScheduleResponseDto getOne(Long id) {
        return toDto(scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("일정 없음")));
    }

    public ScheduleResponseDto update(Long id, ScheduleRequestDto dto) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("일정 없음"));

        schedule.setContent(dto.getContent());
        schedule.setDueDate(dto.getDueDate());
        schedule.setStatus(dto.getStatus());

        //팀 일정 상태 변경 알림
        if (schedule.getType() == Schedule.ScheduleType.TEAM) {
            Team team = schedule.getTeam();
            List<User> members = teamRepository.findMembersByTeamId(team.getId());
            for (User member : members) {
                if (!member.getId().equals(schedule.getUser().getId())) {
                    notificationService.sendNotification(
                            member.getId(),
                            NotificationType.TEAM_SCHEDULE_UPDATE,
                            "'" + team.getName() + "' " + schedule.getUser().getName() +
                                    "님의 '" + schedule.getContent() + "' 상태가 " + schedule.getStatus() + "로 변경되었습니다.",
                            null
                    );
                }
            }
        }

        return toDto(schedule);
    }

    public void delete(Long id) {
        scheduleRepository.deleteById(id);
    }

    private ScheduleResponseDto toDto(Schedule schedule) {
        return ScheduleResponseDto.builder()
                .id(schedule.getId())
                .content(schedule.getContent())
                .dueDate(schedule.getDueDate())
                .status(schedule.getStatus())
                .type(schedule.getType())
                .author(
                        schedule.getType() == Schedule.ScheduleType.TEAM && schedule.getUser() != null
                                ? schedule.getUser().getName() // 또는 .getEmail()
                                : null
                )
                .build();
    }
}
