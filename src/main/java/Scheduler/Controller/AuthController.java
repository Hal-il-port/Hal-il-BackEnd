package Scheduler.Controller;

import Scheduler.Dto.JwtResponseDto;
import Scheduler.Dto.UserLoginRequestDto;
import Scheduler.Dto.UserSignupRequestDto;
import Scheduler.Service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증", description = "회원가입, 로그인 관련 API")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "이메일, 비밀번호로 회원가입합니다.")
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody UserSignupRequestDto request) {
        authService.signup(request);
        return ResponseEntity.ok().build();
    }
    @Operation(summary = "로그인", description = "이메일, 비밀번호로 로그인하고 JWT 토큰을 반환합니다.")
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@RequestBody UserLoginRequestDto request) {
        String token = authService.login(request);
        return ResponseEntity.ok(new JwtResponseDto(token));
    }
}
