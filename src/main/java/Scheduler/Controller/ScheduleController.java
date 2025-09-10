package Scheduler.Controller;

import Scheduler.Dto.Schedule.ScheduleRequestDto;
import Scheduler.Dto.Schedule.ScheduleResponseDto;
import Scheduler.Service.ScheduleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedules")
@Tag(name = "일정", description = "개인 및 팀 일정 관련 API")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Operation(summary = "일정 생성", description = "사용자의 일정을 생성합니다.")
    @PostMapping
    public ResponseEntity<ScheduleResponseDto> create(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ScheduleRequestDto dto) {
        return ResponseEntity.ok(scheduleService.create(userDetails.getUsername(), dto));
    }

    @Operation(summary = "내 일정 목록 조회", description = "현재 로그인한 사용자의 개인 일정을 조회합니다.")
    @GetMapping("/my")
    public ResponseEntity<List<ScheduleResponseDto>> getMy(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(scheduleService.getMySchedule(userDetails.getUsername()));
    }

    @Operation(summary = "팀 일정 목록 조회", description = "특정 팀의 일정을 조회합니다.")
    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<ScheduleResponseDto>> getTeam(
            @Parameter(description = "조회할 팀 ID", example = "1") @PathVariable Long teamId) {
        return ResponseEntity.ok(scheduleService.getTeamSchedule(teamId));
    }

    @Operation(summary = "일정 상세 조회", description = "특정 일정 ID로 일정을 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleResponseDto> getOne(
            @Parameter(description = "조회할 일정 ID", example = "5") @PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.getOne(id));
    }

    @Operation(summary = "일정 수정", description = "일정 ID를 기준으로 내용을 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<ScheduleResponseDto> update(
            @Parameter(description = "수정할 일정 ID", example = "5") @PathVariable Long id,
            @RequestBody ScheduleRequestDto dto) {
        return ResponseEntity.ok(scheduleService.update(id, dto));
    }

    @Operation(summary = "일정 삭제", description = "일정 ID를 기준으로 해당 일정을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "삭제할 일정 ID", example = "5") @PathVariable Long id) {
        scheduleService.delete(id);
        return ResponseEntity.ok().build();
    }

    @Value("${HOLIDAY_API_KEY}")
    private String serviceKey;

    @Operation(summary = "공휴일", description = "공휴일 공공데이터를 가져옵니다.")
    @GetMapping("/holidays")
    public ResponseEntity<Object> getHolidays(@RequestParam int year, @RequestParam int month) {
        try {
            String url = "https://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo"
                    + "?ServiceKey=" + serviceKey
                    + "&solYear=" + year
                    + "&solMonth=" + String.format("%02d", month)
                    + "&_type=json";

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            ObjectMapper mapper = new ObjectMapper();
            Object json = mapper.readValue(response.getBody(), Object.class);

            return ResponseEntity.ok(json);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("공휴일 API 호출 실패: " + e.getMessage());
        }
    }
}
