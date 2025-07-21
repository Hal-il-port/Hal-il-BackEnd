package Scheduler.Controller;

import Scheduler.Service.InviteSignupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/signup")
@Tag(name = "회원가입", description = "초대 토큰 기반 비회원 회원가입 API")
public class SignupController {

    private final InviteSignupService inviteSignupService;

    @Operation(summary = "회원가입 요청", description = "이름, 이메일, 비밀번호, 초대 토큰으로 회원가입을 처리합니다.")
    @PostMapping
    public ResponseEntity<Void> signup(@RequestBody SignupRequestDto request) {
        inviteSignupService.signup(request.getName(), request.getEmail(), request.getPassword(), request.getInviteToken());
        return ResponseEntity.ok().build();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignupRequestDto {

        @Schema(description = "사용자 이름", example = "김원종")
        private String name;

        @Schema(description = "사용자 이메일", example = "user@example.com")
        private String email;

        @Schema(description = "비밀번호", example = "password123!")
        private String password;

        @Schema(description = "초대 토큰 (없으면 null 가능)", example = "INVITE_TOKEN_1234")
        private String inviteToken;
    }
}
