package Scheduler.Controller;

import Scheduler.Entity.Notification;
import Scheduler.Service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "알림", description = "알림 관련 API")
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(
            summary = "알림 목록 조회",
            description = "로그인한 사용자의 알림 목록을 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "알림 목록 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Notification.class)
                            )
                    )
            }
    )
    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(
            @Parameter(description = "인증된 사용자 정보", required = true)
            @AuthenticationPrincipal UserDetails userDetails) {
        List<Notification> notifications = notificationService.getNotifications(
                Long.valueOf(userDetails.getUsername()));
        return ResponseEntity.ok(notifications);
    }

    @Operation(
            summary = "알림 읽음 처리",
            description = "지정한 알림을 읽음 처리합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "읽음 처리 성공"),
                    @ApiResponse(responseCode = "404", description = "알림을 찾을 수 없음")
            }
    )
    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @Parameter(description = "읽음 처리할 알림 ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "인증된 사용자 정보", required = true)
            @AuthenticationPrincipal UserDetails userDetails) {
        notificationService.markAsRead(id, Long.valueOf(userDetails.getUsername()));
        return ResponseEntity.ok().build();
    }
}
