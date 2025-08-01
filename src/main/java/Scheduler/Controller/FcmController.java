package Scheduler.Controller;

import Scheduler.Service.FcmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fcm")
@Tag(name = "FCM API", description = "Firebase 클라우드 메시징 관련 API")
public class FcmController {

    private final FcmService fcmService;

    @Operation(
            summary = "FCM 토큰 등록",
            description = "사용자 Firebase 클라이언트 토큰을 등록합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "토큰 등록 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청")
            }
    )

    // 클라이언트에서 토큰 등록
    @PostMapping("/token")
    public ResponseEntity<Void> registerToken(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> request
    )
    {
        Long userId = Long.parseLong(userDetails.getUsername()); // JWT에서 가져온 유저 ID
        String token = request.get("token");
        fcmService.saveToken(userId, token);
        return ResponseEntity.ok().build();
    }
}
