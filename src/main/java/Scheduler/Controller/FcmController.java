package Scheduler.Controller;

import Scheduler.Service.FcmService;
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
public class FcmController {

    private final FcmService fcmService;

    // 클라이언트에서 토큰 등록
    @PostMapping("/token")
    public ResponseEntity<Void> registerToken(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> request
    ) {
        Long userId = Long.parseLong(userDetails.getUsername()); // JWT에서 가져온 유저 ID
        String token = request.get("token");
        fcmService.saveToken(userId, token);
        return ResponseEntity.ok().build();
    }
}
