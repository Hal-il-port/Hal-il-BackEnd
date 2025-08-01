package Scheduler.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FcmRequestDto {
    @Schema(description = "Firebase 클라이언트 토큰", example = "abcdef123456")
    private String token;
}
