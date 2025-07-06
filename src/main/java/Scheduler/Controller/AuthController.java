package Scheduler.Controller;

import Scheduler.Dto.JwtResponseDto;
import Scheduler.Dto.UserLoginRequestDto;
import Scheduler.Dto.UserSignupRequestDto;
import Scheduler.Service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody UserSignupRequestDto request) {
        authService.signup(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@RequestBody UserLoginRequestDto request) {
        String token = authService.login(request);
        return ResponseEntity.ok(new JwtResponseDto(token));
    }
}
