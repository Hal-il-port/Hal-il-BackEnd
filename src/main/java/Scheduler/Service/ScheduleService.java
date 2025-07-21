package Scheduler.Service;

import Scheduler.Dto.Schedule.ScheduleRequestDto;
import Scheduler.Dto.Schedule.ScheduleResponseDto;
import Scheduler.Entity.Schedule;
import Scheduler.Entity.User;
import Scheduler.Repository.TeamRepository;
import Scheduler.Repository.ScheduleRepository;
import Scheduler.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    public ScheduleResponseDto create(String email, ScheduleRequestDto dto) {
        System.out.println("create() 호출됨");
        System.out.println("email = " + email);
        System.out.println("dto.content = " + dto.getContent());
        System.out.println("dto.dueDate = " + dto.getDueDate());
        System.out.println("dto.status = " + dto.getStatus());
        System.out.println("dto.type = " + dto.getType());
        System.out.println("dto.teamId = " + dto.getTeamId());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        Schedule.ScheduleType type = dto.getType();

        Schedule schedule = Schedule.builder()
                .content(dto.getContent())
                .dueDate(dto.getDueDate())
                .status(dto.getStatus())
                .type(type)
                .user(user)  // 개인/팀 일정 모두 작성자 저장
                .team(type == Schedule.ScheduleType.TEAM ? teamRepository.findById(dto.getTeamId())
                        .orElseThrow(() -> new IllegalArgumentException("팀 없음")) : null)
                .build();
        return toDto(scheduleRepository.save(schedule));
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
