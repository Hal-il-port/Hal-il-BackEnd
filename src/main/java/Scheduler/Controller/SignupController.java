package Scheduler.Controller;

import Scheduler.Service.InviteSignupService;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/signup")
public class SignupController {

    private final InviteSignupService inviteSignupService;

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
        private String name;
        private String email;
        private String password;
        private String inviteToken; // null or 존재할 경우
    }
}
